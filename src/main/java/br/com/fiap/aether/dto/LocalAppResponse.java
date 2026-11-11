package br.com.fiap.aether.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Local no formato consumido pelo app mobile (Flutter): categoria como rótulo
 * legível e recursos de acessibilidade como lista de códigos.
 */
@Schema(description = "Local no formato do app mobile INCLUZE")
public class LocalAppResponse {

    @Schema(example = "1")
    private Long id;

    @Schema(example = "Café Encontro Sensorial")
    private String nome;

    @Schema(example = "Restaurante", description = "Rótulo legível da categoria")
    private String categoria;

    @Schema(example = "Rua Augusta, 1200")
    private String endereco;

    @Schema(example = "Consolação")
    private String bairro;

    private String descricao;

    @Schema(description = "URL da imagem do local")
    private String imagem;

    @Schema(example = "4.7", description = "Nota média (0 quando ainda não há avaliações)")
    private Double nota;

    @Schema(example = "6", description = "Quantidade de avaliações recebidas")
    private Integer avaliacoes;

    @Schema(example = "true", description = "Ao menos 3 avaliações confirmaram a acessibilidade no local")
    private Boolean verificado;

    @Schema(example = "[\"cadeirante\", \"braille\", \"cao_guia\"]",
            description = "Códigos: cadeirante, banheiro_adaptado, elevador, piso_tatil, libras, braille, cao_guia, baixo_estimulo")
    private List<String> recursos;

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

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public Double getNota() {
        return nota;
    }

    public void setNota(Double nota) {
        this.nota = nota;
    }

    public Integer getAvaliacoes() {
        return avaliacoes;
    }

    public void setAvaliacoes(Integer avaliacoes) {
        this.avaliacoes = avaliacoes;
    }

    public Boolean getVerificado() {
        return verificado;
    }

    public void setVerificado(Boolean verificado) {
        this.verificado = verificado;
    }

    public List<String> getRecursos() {
        return recursos;
    }

    public void setRecursos(List<String> recursos) {
        this.recursos = recursos;
    }
}
