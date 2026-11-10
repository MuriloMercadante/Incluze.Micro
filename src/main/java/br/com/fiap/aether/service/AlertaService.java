package br.com.fiap.aether.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.fiap.aether.dto.AlertaRequest;
import br.com.fiap.aether.dto.AlertaResponse;
import br.com.fiap.aether.exception.ResourceNotFoundException;
import br.com.fiap.aether.mapper.AlertaMapper;
import br.com.fiap.aether.model.Alerta;
import br.com.fiap.aether.model.Local;
import br.com.fiap.aether.model.Usuario;
import br.com.fiap.aether.repository.AlertaRepository;
import br.com.fiap.aether.repository.LocalRepository;
import br.com.fiap.aether.repository.UsuarioRepository;

/**
 * Regras de negócio dos alertas temporários ("Waze da inclusão"):
 * - todo alerta nasce ativo (salvo se informado o contrário);
 * - reportar um alerta concede pontos de gamificação ao usuário;
 * - alertas com data de expiração vencida são desativados automaticamente.
 */
@Service
@Transactional
public class AlertaService {

    public static final int PONTOS_POR_ALERTA = 5;

    private final AlertaRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final LocalRepository localRepository;
    private final AlertaMapper mapper;

    public AlertaService(AlertaRepository repository, UsuarioRepository usuarioRepository,
            LocalRepository localRepository, AlertaMapper mapper) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
        this.localRepository = localRepository;
        this.mapper = mapper;
    }

    public List<AlertaResponse> findAll() {
        return repository.findAll().stream()
                .map(this::desativarSeExpirado)
                .map(mapper::toDto)
                .toList();
    }

    public AlertaResponse findById(Long id) {
        return mapper.toDto(desativarSeExpirado(buscar(id)));
    }

    public AlertaResponse create(AlertaRequest request) {
        Usuario usuario = buscarUsuario(request.getUsuarioId());
        Local local = buscarLocal(request.getLocalId());

        Alerta alerta = mapper.toModel(request);
        alerta.setUsuario(usuario);
        alerta.setLocal(local);
        alerta.setAtivo(request.getAtivo() == null ? Boolean.TRUE : request.getAtivo());
        alerta.setDataCriacao(LocalDateTime.now());
        Alerta salvo = repository.save(alerta);

        usuario.setPontos(usuario.getPontos() + PONTOS_POR_ALERTA);

        return mapper.toDto(salvo);
    }

    public AlertaResponse update(Long id, AlertaRequest request) {
        Alerta alerta = buscar(id);
        Usuario usuarioAnterior = alerta.getUsuario();
        Boolean ativoAtual = alerta.getAtivo();

        Usuario usuario = buscarUsuario(request.getUsuarioId());
        Local local = buscarLocal(request.getLocalId());

        // dataCriacao é controlada pela API e não muda na atualização
        mapper.updateModel(request, alerta);
        alerta.setUsuario(usuario);
        alerta.setLocal(local);
        alerta.setAtivo(request.getAtivo() == null ? ativoAtual : request.getAtivo());
        Alerta salvo = repository.save(alerta);

        if (!usuarioAnterior.getId().equals(usuario.getId())) {
            removerPontos(usuarioAnterior, PONTOS_POR_ALERTA);
            usuario.setPontos(usuario.getPontos() + PONTOS_POR_ALERTA);
        }

        return mapper.toDto(salvo);
    }

    public void deleteById(Long id) {
        Alerta alerta = buscar(id);
        Usuario usuario = alerta.getUsuario();
        repository.delete(alerta);
        removerPontos(usuario, PONTOS_POR_ALERTA);
    }

    private Alerta desativarSeExpirado(Alerta alerta) {
        if (Boolean.TRUE.equals(alerta.getAtivo())
                && alerta.getDataExpiracao().isBefore(LocalDateTime.now())) {
            alerta.setAtivo(false); // persistido ao final da transação
        }
        return alerta;
    }

    private void removerPontos(Usuario usuario, int pontos) {
        usuario.setPontos(Math.max(0, usuario.getPontos() - pontos));
    }

    private Alerta buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alerta", id));
    }

    private Usuario buscarUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", id));
    }

    private Local buscarLocal(Long id) {
        return localRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Local", id));
    }
}
