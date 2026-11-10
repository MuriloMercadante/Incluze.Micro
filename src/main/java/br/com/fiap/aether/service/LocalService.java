package br.com.fiap.aether.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.fiap.aether.dto.LocalRequest;
import br.com.fiap.aether.dto.LocalResponse;
import br.com.fiap.aether.exception.BusinessException;
import br.com.fiap.aether.exception.ResourceNotFoundException;
import br.com.fiap.aether.mapper.LocalMapper;
import br.com.fiap.aether.model.Local;
import br.com.fiap.aether.repository.AlertaRepository;
import br.com.fiap.aether.repository.AvaliacaoRepository;
import br.com.fiap.aether.repository.LocalRepository;

@Service
@Transactional
public class LocalService {

    private final LocalRepository repository;
    private final AvaliacaoRepository avaliacaoRepository;
    private final AlertaRepository alertaRepository;
    private final LocalMapper mapper;

    public LocalService(LocalRepository repository, AvaliacaoRepository avaliacaoRepository,
            AlertaRepository alertaRepository, LocalMapper mapper) {
        this.repository = repository;
        this.avaliacaoRepository = avaliacaoRepository;
        this.alertaRepository = alertaRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<LocalResponse> findAll() {
        return repository.findAll().stream().map(mapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public LocalResponse findById(Long id) {
        return mapper.toDto(buscar(id));
    }

    public LocalResponse create(LocalRequest request) {
        Local local = mapper.toModel(request);
        local.setNotaMedia(null); // sem avaliações ainda
        local.setDataCadastro(LocalDateTime.now());
        return mapper.toDto(repository.save(local));
    }

    public LocalResponse update(Long id, LocalRequest request) {
        Local local = buscar(id);
        // notaMedia e dataCadastro são controlados pela API e não mudam na atualização
        mapper.updateModel(request, local);
        return mapper.toDto(repository.save(local));
    }

    public void deleteById(Long id) {
        Local local = buscar(id);
        if (avaliacaoRepository.existsByLocalId(id) || alertaRepository.existsByLocalId(id)) {
            throw new BusinessException(
                    "Local possui avaliações ou alertas vinculados. Exclua-os antes de remover o local");
        }
        repository.delete(local);
    }

    private Local buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Local", id));
    }
}
