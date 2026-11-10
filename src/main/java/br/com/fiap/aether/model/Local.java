package br.com.fiap.aether.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Espaco publico ou privado mapeado pela comunidade, com os recursos de
 * acessibilidade fisica e de "inclusao invisivel".
 */
@Entity
@Table(name = "locais")
public class Local {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, length = 200)
    private String endereco;

    @Column(nullable = false, length = 80)
    private String cidade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CategoriaLocal categoria;

    @Column(name = "possui_rampa", nullable = false)
    private Boolean possuiRampa;

    @Column(name = "atendimento_libras", nullable = false)
    private Boolean atendimentoLibras;

    @Column(name = "cardapio_braille", nullable = false)
    private Boolean cardapioBraille;

    @Column(name = "aceita_cao_guia", nullable = false)
    private Boolean aceitaCaoGuia;

    @Column(name = "ambiente_baixo_estimulo", nullable = false)
    private Boolean ambienteBaixoEstimulo;

    /** Media das notas recebidas nas avaliacoes (calculada pela Service). */
    @Column(name = "nota_media")
    private Double notaMedia;

    @Column(name = "data_cadastro", nullable = false)
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
