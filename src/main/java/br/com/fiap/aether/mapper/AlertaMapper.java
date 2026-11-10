package br.com.fiap.aether.mapper;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

import br.com.fiap.aether.dto.AlertaRequest;
import br.com.fiap.aether.dto.AlertaResponse;
import br.com.fiap.aether.model.Alerta;

@Component
public class AlertaMapper {

    private final ModelMapper modelMapper = new ModelMapper();

    public AlertaMapper() {
        // STRICT: só mapeia propriedades com nomes idênticos (evita ambiguidades)
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        // Os relacionamentos sao resolvidos pela Service a partir dos ids,
        // por isso o mapper nunca deve preencher usuario/local da entidade.
        modelMapper.typeMap(AlertaRequest.class, Alerta.class)
                .addMappings(m -> {
                    m.skip(Alerta::setUsuario);
                    m.skip(Alerta::setLocal);
                });

        // Resposta: achata os relacionamentos em ids e nomes
        modelMapper.typeMap(Alerta.class, AlertaResponse.class)
                .addMappings(m -> {
                    m.map(src -> src.getUsuario().getId(), AlertaResponse::setUsuarioId);
                    m.map(src -> src.getUsuario().getNome(), AlertaResponse::setUsuarioNome);
                    m.map(src -> src.getLocal().getId(), AlertaResponse::setLocalId);
                    m.map(src -> src.getLocal().getNome(), AlertaResponse::setLocalNome);
                });
    }

    public Alerta toModel(AlertaRequest dto) {
        return modelMapper.map(dto, Alerta.class);
    }

    /** Copia os dados do request para uma entidade já existente (atualização). */
    public void updateModel(AlertaRequest dto, Alerta entity) {
        modelMapper.map(dto, entity);
    }

    public AlertaResponse toDto(Alerta entity) {
        return modelMapper.map(entity, AlertaResponse.class);
    }
}
