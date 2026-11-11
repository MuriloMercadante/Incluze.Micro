package br.com.fiap.aether.dto;

import br.com.fiap.aether.model.CategoriaLocal;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

/** Filtros opcionais (query string) da listagem de locais. */
public record LocalFiltro(
        @Schema(description = "Cidade (sem diferenciar maiúsculas)", example = "São Paulo")
        String cidade,

        @Schema(description = "Categoria do local", example = "RESTAURANTE")
        CategoriaLocal categoria,

        @Schema(description = "Somente locais com rampa", example = "true")
        Boolean possuiRampa,

        @Schema(description = "Somente locais com atendimento em Libras", example = "true")
        Boolean atendimentoLibras,

        @Schema(description = "Somente locais com cardápio em Braille")
        Boolean cardapioBraille,

        @Schema(description = "Somente locais que aceitam cão-guia")
        Boolean aceitaCaoGuia,

        @Schema(description = "Somente ambientes de baixo estímulo (espectro autista)")
        Boolean ambienteBaixoEstimulo,

        @Schema(description = "Somente locais com banheiro adaptado")
        Boolean possuiBanheiroAdaptado,

        @Schema(description = "Somente locais com elevador")
        Boolean possuiElevador,

        @Schema(description = "Somente locais com piso tátil")
        Boolean possuiPisoTatil,

        @Schema(description = "Somente locais verificados pela comunidade")
        Boolean verificado,

        @Schema(description = "Nota média mínima (1 a 5)", example = "4")
        @DecimalMin(value = "1.0", message = "A nota mínima deve ser entre 1 e 5")
        @DecimalMax(value = "5.0", message = "A nota mínima deve ser entre 1 e 5")
        Double notaMinima) {
}
