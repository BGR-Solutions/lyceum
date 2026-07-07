package com.lyceum.academic.infra.api;

import com.lyceum.academic.application.service.CrudService;
import com.lyceum.academic.domain.entity.Discipline;
import com.lyceum.academic.infra.api.dto.ApiErrorResponse;
import com.lyceum.academic.infra.api.dto.DisciplineRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing Disciplines.
 * Provides endpoints to create, retrieve, update, and delete disciplines.
 */
@Tag(name = "Disciplines", description = "Cadastro e gerenciamento de disciplinas vinculadas a cursos")
@RestController
@RequestMapping("/disciplines")
public class DisciplineController {
    private final CrudService crudService;

    public DisciplineController(CrudService crudService) {
        this.crudService = crudService;
    }

    @Operation(summary = "Cadastrar disciplina", description = "Cria uma nova disciplina e a vincula ao curso informado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Disciplina criada com sucesso",
                    content = @Content(schema = @Schema(implementation = Discipline.class))),
            @ApiResponse(responseCode = "400", description = "Payload inválido",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Curso não encontrado",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping
    public Discipline create(@Valid @RequestBody DisciplineRequest request) {
        return crudService.createDiscipline(request);
    }

    @Operation(summary = "Listar disciplinas", description = "Retorna todas as disciplinas cadastradas.")
    @ApiResponse(responseCode = "200", description = "Lista de disciplinas",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Discipline.class))))
    @GetMapping
    public List<Discipline> list() {
        return crudService.listDisciplines();
    }

    @Operation(summary = "Atualizar disciplina", description = "Atualiza o nome e/ou curso da disciplina identificada pelo ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Disciplina atualizada com sucesso",
                    content = @Content(schema = @Schema(implementation = Discipline.class))),
            @ApiResponse(responseCode = "400", description = "Payload inválido",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Disciplina ou curso não encontrados",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public Discipline update(
            @Parameter(description = "ID da disciplina", required = true) @PathVariable UUID id,
            @Valid @RequestBody DisciplineRequest request) {
        return crudService.updateDiscipline(id, request);
    }

    @Operation(summary = "Excluir disciplina", description = "Remove a disciplina do sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Disciplina excluída com sucesso"),
            @ApiResponse(responseCode = "404", description = "Disciplina não encontrada",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public void delete(
            @Parameter(description = "ID da disciplina", required = true) @PathVariable UUID id) {
        crudService.deleteDiscipline(id);
    }
}
