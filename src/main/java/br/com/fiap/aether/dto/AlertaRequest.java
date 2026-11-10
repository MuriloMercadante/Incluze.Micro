package br.com.fiap.aether.dto;

import java.time.LocalDateTime;

import br.com.fiap.aether.model.TipoAlerta;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para cadastro/atualização de alerta temporário de barreira")
public class AlertaRequest {

    @Schema(example = "Elevador da estação fora de serviço")
    @NotBlank(message = "O título é obrigatório")
    @Size(min = 5, max = 120, message = "O título deve ter entre 5 e 120 caracteres")
    private String titulo;

    @Schema(example = "Elevador de acesso à plataforma 2 parado hoje. Usar a entrada da Rua B.")
    @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres")
    private String descricao;

    @Schema(example = "ELEVADOR_QUEBRADO")
    @NotNull(message = "O tipo do alerta é obrigatório")
    private TipoAlerta tipo;

    @Schema(example = "2026-12-31T18:00:00")
    @NotNull(message = "A data de expiração é obrigatória")
    @Future(message = "A data de expiração deve estar no futuro")
    private LocalDateTime dataExpiracao;

    @Schema(example = "true", description = "Opcional. Padrão: true")
    private Boolean ativo;

    @Schema(example = "1")
    @NotNull(message = "O usuarioId é obrigatório")
    @Positive(message = "O usuarioId deve ser positivo")
    private Long usuarioId;

    @Schema(example = "1")
    @NotNull(message = "O localId é obrigatório")
    @Positive(message = "O localId deve ser positivo")
    private Long localId;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public TipoAlerta getTipo() {
        return tipo;
    }

    public void setTipo(TipoAlerta tipo) {
        this.tipo = tipo;
    }

    public LocalDateTime getDataExpiracao() {
        return dataExpiracao;
    }

    public void setDataExpiracao(LocalDateTime dataExpiracao) {
        this.dataExpiracao = dataExpiracao;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
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
