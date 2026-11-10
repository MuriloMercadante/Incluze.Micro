package br.com.fiap.aether.dto;

import java.time.LocalDateTime;

import br.com.fiap.aether.model.CategoriaLocal;

public class LocalResponse {

    private Long id;
    private String nome;
    private String endereco;
    private String cidade;
    private CategoriaLocal categoria;
    private Boolean possuiRampa;
    private Boolean atendimentoLibras;
    private Boolean cardapioBraille;
    private Boolean aceitaCaoGuia;
    private Boolean ambienteBaixoEstimulo;
    private Double notaMedia;
    private LocalDateTime dataCadastro;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Double getNotaMedia() {
        return notaMedia;
    }

    public void setNotaMedia(Double notaMedia) {
        this.notaMedia = notaMedia;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }
}
