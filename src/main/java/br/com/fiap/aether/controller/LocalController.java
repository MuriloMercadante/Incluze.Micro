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

import org.springdoc.core.annotations.ParameterObject;

import br.com.fiap.aether.dto.ErrorResponse;
import br.com.fiap.aether.dto.LocalFiltro;
import br.com.fiap.aether.dto.LocalRequest;
import br.com.fiap.aether.dto.LocalResponse;
import br.com.fiap.aether.service.LocalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/${api.version}/locais")
@Tag(name = "Locais", description = "Espaços públicos e privados com seus recursos de acessibilidade física e invisível")
public class LocalController {

    private final LocalService service;

    public LocalController(LocalService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Cadastrar local")
    @ApiResponse(responseCode = "201", description = "Local criado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<LocalResponse> create(@Valid @RequestBody LocalRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping
    @Operation(summary = "Listar locais (com filtros de acessibilidade)",
            description = "Todos os filtros são opcionais e combináveis. Ex.: ?atendimentoLibras=true&aceitaCaoGuia=true&cidade=São Paulo")
    @ApiResponse(responseCode = "200", description = "Lista de locais")
    @ApiResponse(responseCode = "400", description = "Filtro inválido", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<List<LocalResponse>> findAll(@Valid @ParameterObject LocalFiltro filtro) {
        return ResponseEntity.ok(service.findAll(filtro));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar local por id")
    @ApiResponse(responseCode = "200", description = "Local encontrado")
    @ApiResponse(responseCode = "404", description = "Local não encontrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<LocalResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar local")
    @ApiResponse(responseCode = "200", description = "Local atualizado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Local não encontrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<LocalResponse> update(@PathVariable Long id, @Valid @RequestBody LocalRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir local")
    @ApiResponse(responseCode = "204", description = "Local excluído")
    @ApiResponse(responseCode = "404", description = "Local não encontrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Local possui vínculos", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
