package br.com.fiap.aether.mapper;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

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
}
