package br.com.fiap.aether.mapper;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

import br.com.fiap.aether.dto.AvaliacaoRequest;
import br.com.fiap.aether.dto.AvaliacaoResponse;
import br.com.fiap.aether.model.Avaliacao;

@Component
public class AvaliacaoMapper {

    private final ModelMapper modelMapper = new ModelMapper();

    public AvaliacaoMapper() {
        // STRICT: só mapeia propriedades com nomes idênticos (evita ambiguidades)
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        // Os relacionamentos sao resolvidos pela Service a partir dos ids,
        // por isso o mapper nunca deve preencher usuario/local da entidade.
        modelMapper.typeMap(AvaliacaoRequest.class, Avaliacao.class)
                .addMappings(m -> {
                    m.skip(Avaliacao::setUsuario);
                    m.skip(Avaliacao::setLocal);
                });

        // Resposta: achata os relacionamentos em ids e nomes
        modelMapper.typeMap(Avaliacao.class, AvaliacaoResponse.class)
                .addMappings(m -> {
                    m.map(src -> src.getUsuario().getId(), AvaliacaoResponse::setUsuarioId);
                    m.map(src -> src.getUsuario().getNome(), AvaliacaoResponse::setUsuarioNome);
                    m.map(src -> src.getLocal().getId(), AvaliacaoResponse::setLocalId);
                    m.map(src -> src.getLocal().getNome(), AvaliacaoResponse::setLocalNome);
                });
    }

    public Avaliacao toModel(AvaliacaoRequest dto) {
        return modelMapper.map(dto, Avaliacao.class);
    }

    /** Copia os dados do request para uma entidade já existente (atualização). */
    public void updateModel(AvaliacaoRequest dto, Avaliacao entity) {
        modelMapper.map(dto, entity);
    }

    public AvaliacaoResponse toDto(Avaliacao entity) {
        return modelMapper.map(entity, AvaliacaoResponse.class);
    }
}
