package br.com.fiap.aether.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.fiap.aether.dto.UsuarioRequest;
import br.com.fiap.aether.dto.UsuarioResponse;
import br.com.fiap.aether.exception.BusinessException;
import br.com.fiap.aether.exception.ResourceNotFoundException;
import br.com.fiap.aether.mapper.UsuarioMapper;
import br.com.fiap.aether.model.Usuario;
import br.com.fiap.aether.repository.AlertaRepository;
import br.com.fiap.aether.repository.AvaliacaoRepository;
import br.com.fiap.aether.repository.UsuarioRepository;

@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository repository;
    private final AvaliacaoRepository avaliacaoRepository;
    private final AlertaRepository alertaRepository;
    private final UsuarioMapper mapper;

    public UsuarioService(UsuarioRepository repository, AvaliacaoRepository avaliacaoRepository,
            AlertaRepository alertaRepository, UsuarioMapper mapper) {
        this.repository = repository;
        this.avaliacaoRepository = avaliacaoRepository;
        this.alertaRepository = alertaRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> findAll() {
        return repository.findAll().stream().map(mapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse findById(Long id) {
        return mapper.toDto(buscar(id));
    }

    public UsuarioResponse create(UsuarioRequest request) {
        String email = normalizarEmail(request.getEmail());
        if (repository.existsByEmailIgnoreCase(email)) {
            throw new BusinessException("Já existe um usuário cadastrado com o e-mail informado");
        }

        Usuario usuario = mapper.toModel(request);
        usuario.setEmail(email);
        usuario.setPontos(0);
        usuario.setDataCadastro(LocalDateTime.now());
        return mapper.toDto(repository.save(usuario));
    }

    public UsuarioResponse update(Long id, UsuarioRequest request) {
        Usuario usuario = buscar(id);
        String email = normalizarEmail(request.getEmail());
        if (repository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new BusinessException("Já existe outro usuário cadastrado com o e-mail informado");
        }

        // pontos e dataCadastro são controlados pela API e não mudam na atualização
        mapper.updateModel(request, usuario);
        usuario.setEmail(email);
        return mapper.toDto(repository.save(usuario));
    }

    public void deleteById(Long id) {
        Usuario usuario = buscar(id);
        if (avaliacaoRepository.existsByUsuarioId(id) || alertaRepository.existsByUsuarioId(id)) {
            throw new BusinessException(
                    "Usuário possui avaliações ou alertas vinculados. Exclua-os antes de remover o usuário");
        }
        repository.delete(usuario);
    }

    private Usuario buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", id));
    }

    private String normalizarEmail(String email) {
        return email.trim().toLowerCase();
    }
}
