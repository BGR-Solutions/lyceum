package com.lyceum.academic.infra.api;

import com.lyceum.academic.application.service.CrudService;
import com.lyceum.academic.domain.entity.Course;
import com.lyceum.academic.infra.api.dto.ApiErrorResponse;
import com.lyceum.academic.infra.api.dto.CourseRequest;
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
 * REST controller for managing Courses.
 * Provides endpoints to create, retrieve, update, and delete courses.
 */
@Tag(name = "Courses", description = "Cadastro e gerenciamento de cursos")
@RestController
@RequestMapping("/courses")
public class CourseController {
    private final CrudService crudService;

    public CourseController(CrudService crudService) {
        this.crudService = crudService;
    }

    @Operation(summary = "Cadastrar curso", description = "Cria um novo curso com o nome informado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Curso criado com sucesso",
                    content = @Content(schema = @Schema(implementation = Course.class))),
            @ApiResponse(responseCode = "400", description = "Payload inválido — nome em branco",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping
    public Course create(@Valid @RequestBody CourseRequest request) {
        return crudService.createCourse(request);
    }

    @Operation(summary = "Listar cursos", description = "Retorna todos os cursos cadastrados.")
    @ApiResponse(responseCode = "200", description = "Lista de cursos",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Course.class))))
    @GetMapping
    public List<Course> list() {
        return crudService.listCourses();
    }

    @Operation(summary = "Atualizar curso", description = "Atualiza o nome do curso identificado pelo ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Curso atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = Course.class))),
            @ApiResponse(responseCode = "400", description = "Payload inválido",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Curso não encontrado",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public Course update(
            @Parameter(description = "ID do curso", required = true) @PathVariable UUID id,
            @Valid @RequestBody CourseRequest request) {
        return crudService.updateCourse(id, request);
    }

    @Operation(summary = "Excluir curso", description = "Remove o curso do sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Curso excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Curso não encontrado",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public void delete(
            @Parameter(description = "ID do curso", required = true) @PathVariable UUID id) {
        crudService.deleteCourse(id);
    }
}
