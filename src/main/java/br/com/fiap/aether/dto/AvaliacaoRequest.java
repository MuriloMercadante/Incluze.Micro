package br.com.fiap.aether.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para cadastro/atualização de avaliação de acessibilidade")
public class AvaliacaoRequest {

    @Schema(example = "4")
    @NotNull(message = "A nota é obrigatória")
    @Min(value = 1, message = "A nota mínima é 1")
    @Max(value = 5, message = "A nota máxima é 5")
    private Integer nota;

    @Schema(example = "Atendente fluente em Libras e cardápio com fonte ampliada.")
    @Size(max = 500, message = "O comentário deve ter no máximo 500 caracteres")
    private String comentario;

    @Schema(example = "true")
    @NotNull(message = "Informe se a acessibilidade foi confirmada no local")
    private Boolean acessibilidadeConfirmada;

    @Schema(example = "1")
    @NotNull(message = "O usuarioId é obrigatório")
    @Positive(message = "O usuarioId deve ser positivo")
    private Long usuarioId;

    @Schema(example = "1")
    @NotNull(message = "O localId é obrigatório")
    @Positive(message = "O localId deve ser positivo")
    private Long localId;

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

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getLocalId() {
        return localId;
    }

    public void setLocalId(Long localId) {
        this.localId = localId;
    }
}
