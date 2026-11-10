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
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.aether.dto.AvaliacaoRequest;
import br.com.fiap.aether.dto.AvaliacaoResponse;
import br.com.fiap.aether.dto.ErrorResponse;
import br.com.fiap.aether.service.AvaliacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/${api.version}/avaliacoes")
@Tag(name = "Avaliações", description = "Avaliações de acessibilidade feitas pela comunidade (+10 pontos por avaliação)")
public class AvaliacaoController {

    private final AvaliacaoService service;

    public AvaliacaoController(AvaliacaoService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Cadastrar avaliação", description = "Concede pontos ao usuário e recalcula a nota média do local")
    @ApiResponse(responseCode = "201", description = "Avaliação criada")
    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Usuário ou local não encontrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Usuário já avaliou o local", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<AvaliacaoResponse> create(@Valid @RequestBody AvaliacaoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping
    @Operation(summary = "Listar avaliações")
    @ApiResponse(responseCode = "200", description = "Lista de avaliações")
    public ResponseEntity<List<AvaliacaoResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar avaliação por id")
    @ApiResponse(responseCode = "200", description = "Avaliação encontrada")
    @ApiResponse(responseCode = "404", description = "Avaliação não encontrada", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<AvaliacaoResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar avaliação", description = "Recalcula a nota média do(s) local(is) envolvido(s)")
    @ApiResponse(responseCode = "200", description = "Avaliação atualizada")
    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Avaliação, usuário ou local não encontrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Usuário já possui outra avaliação para o local", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<AvaliacaoResponse> update(@PathVariable Long id, @Valid @RequestBody AvaliacaoRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir avaliação", description = "Remove os pontos concedidos e recalcula a nota média do local")
    @ApiResponse(responseCode = "204", description = "Avaliação excluída")
    @ApiResponse(responseCode = "404", description = "Avaliação não encontrada", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
