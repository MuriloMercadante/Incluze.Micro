package br.com.fiap.aether.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.fiap.aether.dto.LocalAppResponse;
import br.com.fiap.aether.dto.LocalFiltro;
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
    public List<LocalResponse> findAll(LocalFiltro f) {
        String cidade = (f.cidade() == null || f.cidade().isBlank()) ? null : f.cidade().trim();
        return repository.buscar(cidade, f.categoria(), f.possuiRampa(), f.atendimentoLibras(),
                f.cardapioBraille(), f.aceitaCaoGuia(), f.ambienteBaixoEstimulo(),
                f.possuiBanheiroAdaptado(), f.possuiElevador(), f.possuiPisoTatil(),
                f.verificado(), f.notaMinima())
                .stream().map(mapper::toDto).toList();
    }

    /** Todos os locais no formato consumido pelo app mobile. */
    @Transactional(readOnly = true)
    public List<LocalAppResponse> findAllApp() {
        return repository.findAll(Sort.by("id")).stream().map(mapper::toAppDto).toList();
    }

    @Transactional(readOnly = true)
    public LocalResponse findById(Long id) {
        return mapper.toDto(buscar(id));
    }

    public LocalResponse create(LocalRequest request) {
        Local local = mapper.toModel(request);
        normalizarRecursosOpcionais(local);
        // indicadores controlados pela API: começam zerados
        local.setNotaMedia(null);
        local.setTotalAvaliacoes(0);
        local.setVerificado(false);
        local.setDataCadastro(LocalDateTime.now());
        return mapper.toDto(repository.save(local));
    }

    public LocalResponse update(Long id, LocalRequest request) {
        Local local = buscar(id);
        // notaMedia, totalAvaliacoes, verificado e dataCadastro são controlados
        // pela API e não fazem parte do request, então não mudam na atualização
        mapper.updateModel(request, local);
        normalizarRecursosOpcionais(local);
        return mapper.toDto(repository.save(local));
    }

    /** Recursos opcionais no request: ausente = não possui. */
    private void normalizarRecursosOpcionais(Local local) {
        local.setPossuiBanheiroAdaptado(Boolean.TRUE.equals(local.getPossuiBanheiroAdaptado()));
        local.setPossuiElevador(Boolean.TRUE.equals(local.getPossuiElevador()));
        local.setPossuiPisoTatil(Boolean.TRUE.equals(local.getPossuiPisoTatil()));
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
