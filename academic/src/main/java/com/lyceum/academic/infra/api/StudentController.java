package com.lyceum.academic.infra.api;

import com.lyceum.academic.application.service.CrudService;
import com.lyceum.academic.domain.entity.Student;
import com.lyceum.academic.infra.api.dto.ApiErrorResponse;
import com.lyceum.academic.infra.api.dto.StudentRequest;
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
 * REST controller for managing Students.
 * Provides endpoints to create, retrieve, update, and delete students.
 */
@Tag(name = "Students", description = "Cadastro e gerenciamento de alunos")
@RestController
@RequestMapping("/students")
public class StudentController {
    private final CrudService crudService;

    public StudentController(CrudService crudService) {
        this.crudService = crudService;
    }

    @Operation(summary = "Cadastrar aluno", description = "Cria um novo aluno com o nome informado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Aluno criado com sucesso",
                    content = @Content(schema = @Schema(implementation = Student.class))),
            @ApiResponse(responseCode = "400", description = "Payload inválido — nome em branco",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping
    public Student create(@Valid @RequestBody StudentRequest request) {
        return crudService.createStudent(request);
    }

    @Operation(summary = "Listar alunos", description = "Retorna todos os alunos cadastrados.")
    @ApiResponse(responseCode = "200", description = "Lista de alunos",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Student.class))))
    @GetMapping
    public List<Student> list() {
        return crudService.listStudents();
    }

    @Operation(summary = "Atualizar aluno", description = "Atualiza o nome do aluno identificado pelo ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Aluno atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = Student.class))),
            @ApiResponse(responseCode = "400", description = "Payload inválido",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Aluno não encontrado",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public Student update(
            @Parameter(description = "ID do aluno", required = true) @PathVariable UUID id,
            @Valid @RequestBody StudentRequest request) {
        return crudService.updateStudent(id, request);
    }

    @Operation(summary = "Excluir aluno", description = "Remove o aluno do sistema. Não é possível excluir alunos com matrículas ativas.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Aluno excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Aluno não encontrado",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public void delete(
            @Parameter(description = "ID do aluno", required = true) @PathVariable UUID id) {
        crudService.deleteStudent(id);
    }
}
