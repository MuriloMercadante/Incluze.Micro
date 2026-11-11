package br.com.fiap.aether.mapper;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

import br.com.fiap.aether.dto.LocalAppResponse;
import br.com.fiap.aether.dto.LocalRequest;
import br.com.fiap.aether.dto.LocalResponse;
import br.com.fiap.aether.model.Local;

@Component
public class LocalMapper {

    private final ModelMapper modelMapper = new ModelMapper();

    public LocalMapper() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    public Local toModel(LocalRequest dto) {
        return modelMapper.map(dto, Local.class);
    }

    /** Copia os dados do request para uma entidade já existente (atualização). */
    public void updateModel(LocalRequest dto, Local entity) {
        modelMapper.map(dto, entity);
    }

    public LocalResponse toDto(Local entity) {
        return modelMapper.map(entity, LocalResponse.class);
    }

    /** Converte para o formato consumido pelo app mobile (Flutter). */
    public LocalAppResponse toAppDto(Local entity) {
        LocalAppResponse dto = new LocalAppResponse();
        dto.setId(entity.getId());
        dto.setNome(entity.getNome());
        dto.setCategoria(entity.getCategoria().getRotulo());
        dto.setEndereco(entity.getEndereco());
        dto.setBairro(valorOuVazio(entity.getBairro()));
        dto.setDescricao(valorOuVazio(entity.getDescricao()));
        dto.setImagem(valorOuVazio(entity.getImagemUrl()));
        dto.setNota(entity.getNotaMedia() == null ? 0.0 : entity.getNotaMedia());
        dto.setAvaliacoes(entity.getTotalAvaliacoes() == null ? 0 : entity.getTotalAvaliacoes());
        dto.setVerificado(Boolean.TRUE.equals(entity.getVerificado()));
        dto.setRecursos(recursos(entity));
        return dto;
    }

    /** Recursos de acessibilidade como códigos, na mesma ordem exibida pelo app. */
    private List<String> recursos(Local l) {
        List<String> codigos = new ArrayList<>();
        adicionarSe(codigos, l.getPossuiRampa(), "cadeirante");
        adicionarSe(codigos, l.getPossuiBanheiroAdaptado(), "banheiro_adaptado");
        adicionarSe(codigos, l.getPossuiElevador(), "elevador");
        adicionarSe(codigos, l.getPossuiPisoTatil(), "piso_tatil");
        adicionarSe(codigos, l.getAtendimentoLibras(), "libras");
        adicionarSe(codigos, l.getCardapioBraille(), "braille");
        adicionarSe(codigos, l.getAceitaCaoGuia(), "cao_guia");
        adicionarSe(codigos, l.getAmbienteBaixoEstimulo(), "baixo_estimulo");
        return codigos;
    }

    private void adicionarSe(List<String> codigos, Boolean possui, String codigo) {
        if (Boolean.TRUE.equals(possui)) {
            codigos.add(codigo);
        }
    }

    private String valorOuVazio(String valor) {
        return valor == null ? "" : valor;
    }
}
