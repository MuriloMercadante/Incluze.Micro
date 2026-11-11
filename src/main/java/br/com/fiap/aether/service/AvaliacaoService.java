package br.com.fiap.aether.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.fiap.aether.dto.AvaliacaoRequest;
import br.com.fiap.aether.dto.AvaliacaoResponse;
import br.com.fiap.aether.exception.BusinessException;
import br.com.fiap.aether.exception.ReferenciaInvalidaException;
import br.com.fiap.aether.exception.ResourceNotFoundException;
import br.com.fiap.aether.mapper.AvaliacaoMapper;
import br.com.fiap.aether.model.Avaliacao;
import br.com.fiap.aether.model.Local;
import br.com.fiap.aether.model.Usuario;
import br.com.fiap.aether.repository.AvaliacaoRepository;
import br.com.fiap.aether.repository.LocalRepository;
import br.com.fiap.aether.repository.UsuarioRepository;

/**
 * Regras de negócio das avaliações:
 * - cada usuário pode avaliar um mesmo local apenas uma vez;
 * - cada avaliação concede pontos de gamificação ao usuário (e os remove se for excluída);
 * - a nota média e o total de avaliações do local são recalculados a cada inclusão, alteração ou exclusão;
 * - o local recebe o selo "verificado" quando ao menos 3 avaliações confirmam a acessibilidade.
 */
@Service
@Transactional
public class AvaliacaoService {

    public static final int PONTOS_POR_AVALIACAO = 10;
    public static final int MINIMO_CONFIRMACOES_VERIFICADO = 3;

    private final AvaliacaoRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final LocalRepository localRepository;
    private final AvaliacaoMapper mapper;

    public AvaliacaoService(AvaliacaoRepository repository, UsuarioRepository usuarioRepository,
            LocalRepository localRepository, AvaliacaoMapper mapper) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
        this.localRepository = localRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<AvaliacaoResponse> findAll(Long localId, Long usuarioId) {
        return repository.buscar(localId, usuarioId).stream().map(mapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public AvaliacaoResponse findById(Long id) {
        return mapper.toDto(buscar(id));
    }

    public AvaliacaoResponse create(AvaliacaoRequest request) {
        Usuario usuario = buscarUsuario(request.getUsuarioId());
        Local local = buscarLocal(request.getLocalId());

        if (repository.existsByUsuarioIdAndLocalId(usuario.getId(), local.getId())) {
            throw new BusinessException("Este usuário já avaliou este local. Atualize a avaliação existente");
        }

        Avaliacao avaliacao = mapper.toModel(request);
        avaliacao.setUsuario(usuario);
        avaliacao.setLocal(local);
        avaliacao.setDataAvaliacao(LocalDateTime.now());
        Avaliacao salva = repository.save(avaliacao);

        usuario.setPontos(usuario.getPontos() + PONTOS_POR_AVALIACAO);
        atualizarIndicadores(local);

        return mapper.toDto(salva);
    }

    public AvaliacaoResponse update(Long id, AvaliacaoRequest request) {
        Avaliacao avaliacao = buscar(id);
        Usuario usuarioAnterior = avaliacao.getUsuario();
        Local localAnterior = avaliacao.getLocal();

        Usuario usuario = buscarUsuario(request.getUsuarioId());
        Local local = buscarLocal(request.getLocalId());

        if (repository.existsByUsuarioIdAndLocalIdAndIdNot(usuario.getId(), local.getId(), id)) {
            throw new BusinessException("Este usuário já possui outra avaliação para este local");
        }

        mapper.updateModel(request, avaliacao);
        avaliacao.setUsuario(usuario);
        avaliacao.setLocal(local);
        Avaliacao salva = repository.save(avaliacao);

        // se a avaliação mudou de autor, os pontos acompanham
        if (!usuarioAnterior.getId().equals(usuario.getId())) {
            removerPontos(usuarioAnterior, PONTOS_POR_AVALIACAO);
            usuario.setPontos(usuario.getPontos() + PONTOS_POR_AVALIACAO);
        }

        atualizarIndicadores(local);
        if (!localAnterior.getId().equals(local.getId())) {
            atualizarIndicadores(localAnterior);
        }

        return mapper.toDto(salva);
    }

    public void deleteById(Long id) {
        Avaliacao avaliacao = buscar(id);
        Usuario usuario = avaliacao.getUsuario();
        Local local = avaliacao.getLocal();

        repository.delete(avaliacao);

        removerPontos(usuario, PONTOS_POR_AVALIACAO);
        atualizarIndicadores(local);
    }

    /**
     * Recalcula os indicadores do local a partir das avaliações gravadas:
     * nota média, total de avaliações e selo de verificado pela comunidade.
     */
    private void atualizarIndicadores(Local local) {
        Double media = repository.calcularNotaMedia(local.getId());
        local.setNotaMedia(media == null ? null : Math.round(media * 10.0) / 10.0);
        local.setTotalAvaliacoes((int) repository.countByLocalId(local.getId()));
        local.setVerificado(repository.countByLocalIdAndAcessibilidadeConfirmadaTrue(local.getId())
                >= MINIMO_CONFIRMACOES_VERIFICADO);
    }

    private void removerPontos(Usuario usuario, int pontos) {
        usuario.setPontos(Math.max(0, usuario.getPontos() - pontos));
    }

    private Avaliacao buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Avaliação", id));
    }

    /** Ids vindos do corpo: inexistente = conteúdo inválido (422), não recurso da URL (404). */
    private Usuario buscarUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ReferenciaInvalidaException("usuarioId", "usuário", id));
    }

    private Local buscarLocal(Long id) {
        return localRepository.findById(id)
                .orElseThrow(() -> new ReferenciaInvalidaException("localId", "local", id));
    }
}
