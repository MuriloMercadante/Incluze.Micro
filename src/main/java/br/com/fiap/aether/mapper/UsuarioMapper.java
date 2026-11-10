package br.com.fiap.aether.mapper;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

import br.com.fiap.aether.dto.UsuarioRequest;
import br.com.fiap.aether.dto.UsuarioResponse;
import br.com.fiap.aether.model.Usuario;

@Component
public class UsuarioMapper {

    private final ModelMapper modelMapper = new ModelMapper();

    public UsuarioMapper() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    public Usuario toModel(UsuarioRequest dto) {
        return modelMapper.map(dto, Usuario.class);
    }

    /** Copia os dados do request para uma entidade já existente (atualização). */
    public void updateModel(UsuarioRequest dto, Usuario entity) {
        modelMapper.map(dto, entity);
    }

    public UsuarioResponse toDto(Usuario entity) {
        return modelMapper.map(entity, UsuarioResponse.class);
    }
}
