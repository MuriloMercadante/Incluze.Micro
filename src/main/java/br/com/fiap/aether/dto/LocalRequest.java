package br.com.fiap.aether.dto;

import br.com.fiap.aether.model.CategoriaLocal;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para cadastro/atualização de local")
public class LocalRequest {

    @Schema(example = "Restaurante Sabor da Vila")
    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 3, max = 120, message = "O nome deve ter entre 3 e 120 caracteres")
    private String nome;

    @Schema(example = "Rua Augusta, 1500 - Consolação")
    @NotBlank(message = "O endereço é obrigatório")
    @Size(max = 200, message = "O endereço deve ter no máximo 200 caracteres")
    private String endereco;

    @Schema(example = "São Paulo")
    @NotBlank(message = "A cidade é obrigatória")
    @Size(max = 80, message = "A cidade deve ter no máximo 80 caracteres")
    private String cidade;

    @Schema(example = "RESTAURANTE")
    @NotNull(message = "A categoria é obrigatória")
    private CategoriaLocal categoria;

    @Schema(example = "true")
    @NotNull(message = "Informe se o local possui rampa")
    private Boolean possuiRampa;

    @Schema(example = "true")
    @NotNull(message = "Informe se o local possui atendimento em Libras")
    private Boolean atendimentoLibras;

    @Schema(example = "false")
    @NotNull(message = "Informe se o local possui cardápio em Braille")
    private Boolean cardapioBraille;

    @Schema(example = "true")
    @NotNull(message = "Informe se o local aceita cão-guia")
    private Boolean aceitaCaoGuia;

    @Schema(example = "false")
    @NotNull(message = "Informe se o local possui ambiente de baixo estímulo")
    private Boolean ambienteBaixoEstimulo;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public CategoriaLocal getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaLocal categoria) {
        this.categoria = categoria;
    }

    public Boolean getPossuiRampa() {
        return possuiRampa;
    }

    public void setPossuiRampa(Boolean possuiRampa) {
        this.possuiRampa = possuiRampa;
    }

    public Boolean getAtendimentoLibras() {
        return atendimentoLibras;
    }

    public void setAtendimentoLibras(Boolean atendimentoLibras) {
        this.atendimentoLibras = atendimentoLibras;
    }

    public Boolean getCardapioBraille() {
        return cardapioBraille;
    }

    public void setCardapioBraille(Boolean cardapioBraille) {
        this.cardapioBraille = cardapioBraille;
    }

    public Boolean getAceitaCaoGuia() {
        return aceitaCaoGuia;
    }

    public void setAceitaCaoGuia(Boolean aceitaCaoGuia) {
        this.aceitaCaoGuia = aceitaCaoGuia;
    }

    public Boolean getAmbienteBaixoEstimulo() {
        return ambienteBaixoEstimulo;
    }

    public void setAmbienteBaixoEstimulo(Boolean ambienteBaixoEstimulo) {
        this.ambienteBaixoEstimulo = ambienteBaixoEstimulo;
    }
}
