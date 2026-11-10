package br.com.fiap.aether.dto;

import java.time.LocalDateTime;

public class AvaliacaoResponse {

    private Long id;
    private Integer nota;
    private String comentario;
    private Boolean acessibilidadeConfirmada;
    private LocalDateTime dataAvaliacao;
    private Long usuarioId;
    private String usuarioNome;
    private Long localId;
    private String localNome;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNota() {
        return nota;
    }

    public void setNota(Integer nota) {
        this.nota = nota;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Boolean getAcessibilidadeConfirmada() {
        return acessibilidadeConfirmada;
    }

    public void setAcessibilidadeConfirmada(Boolean acessibilidadeConfirmada) {
        this.acessibilidadeConfirmada = acessibilidadeConfirmada;
    }

    public LocalDateTime getDataAvaliacao() {
        return dataAvaliacao;
    }

    public void setDataAvaliacao(LocalDateTime dataAvaliacao) {
        this.dataAvaliacao = dataAvaliacao;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getUsuarioNome() {
        return usuarioNome;
    }

    public void setUsuarioNome(String usuarioNome) {
        this.usuarioNome = usuarioNome;
    }

    public Long getLocalId() {
        return localId;
    }

    public void setLocalId(Long localId) {
        this.localId = localId;
    }

    public String getLocalNome() {
        return localNome;
    }

    public void setLocalNome(String localNome) {
        this.localNome = localNome;
    }
}
