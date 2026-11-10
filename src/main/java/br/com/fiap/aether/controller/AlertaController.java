package br.com.fiap.aether.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.aether.dto.AlertaRequest;
import br.com.fiap.aether.dto.AlertaResponse;
import br.com.fiap.aether.dto.ErrorResponse;
import br.com.fiap.aether.service.AlertaService;
import br.com.fiap.aether.model.TipoAlerta;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/${api.version}/alertas")
@Tag(name = "Alertas", description = "Alertas temporários de barreiras - o \"Waze da inclusão\" (+5 pontos por alerta)")
public class AlertaController {

    private final AlertaService service;

    public AlertaController(AlertaService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Reportar alerta temporário", description = "Concede pontos ao usuário que reportou")
    @ApiResponse(responseCode = "201", description = "Alerta criado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "422", description = "usuarioId ou localId inexistente", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<AlertaResponse> create(@Valid @RequestBody AlertaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping
    @Operation(summary = "Listar alertas",
            description = "Filtros opcionais. Um alerta com data de expiração vencida é apresentado como inativo. "
                    + "Ex.: ?ativo=true&localId=1 retorna as barreiras vigentes de um local.")
    @ApiResponse(responseCode = "200", description = "Lista de alertas (mais recentes primeiro)")
    @ApiResponse(responseCode = "400", description = "Filtro inválido", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<List<AlertaResponse>> findAll(
            @Parameter(description = "Somente alertas deste local") @RequestParam(required = false) Long localId,
            @Parameter(description = "Somente alertas deste tipo") @RequestParam(required = false) TipoAlerta tipo,
            @Parameter(description = "true = vigentes; false = encerrados ou expirados") @RequestParam(required = false) Boolean ativo) {
        return ResponseEntity.ok(service.findAll(localId, tipo, ativo));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar alerta por id")
    @ApiResponse(responseCode = "200", description = "Alerta encontrado")
    @ApiResponse(responseCode = "404", description = "Alerta não encontrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<AlertaResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar alerta")
    @ApiResponse(responseCode = "200", description = "Alerta atualizado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Alerta não encontrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "422", description = "usuarioId ou localId inexistente", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<AlertaResponse> update(@PathVariable Long id, @Valid @RequestBody AlertaRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir alerta", description = "Remove os pontos concedidos ao usuário")
    @ApiResponse(responseCode = "204", description = "Alerta excluído")
    @ApiResponse(responseCode = "404", description = "Alerta não encontrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
