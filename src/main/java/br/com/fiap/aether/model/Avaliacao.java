package br.com.fiap.aether.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Avaliacao de acessibilidade feita por um usuario sobre um local.
 * N avaliacoes -> 1 usuario | N avaliacoes -> 1 local.
 */
@Entity
@Table(name = "avaliacoes")
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nota de 1 a 5 para a acessibilidade do local. */
    @Column(nullable = false)
    private Integer nota;

    @Column(length = 500)
    private String comentario;

    /** Indica se o usuario confirmou no local os recursos declarados. */
    @Column(name = "acessibilidade_confirmada", nullable = false)
    private Boolean acessibilidadeConfirmada;

    @Column(name = "data_avaliacao", nullable = false)
    private LocalDateTime dataAvaliacao;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(optional = false)
    @JoinColumn(name = "local_id", nullable = false)
    private Local local;

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

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Local getLocal() {
        return local;
    }

    public void setLocal(Local local) {
        this.local = local;
    }
}
