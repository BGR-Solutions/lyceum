package com.lyceum.academic.infra.api;

import com.lyceum.academic.application.ports.ClassroomRepository;
import com.lyceum.academic.application.service.CrudService;
import com.lyceum.academic.domain.entity.Classroom;
import com.lyceum.academic.infra.api.dto.ApiErrorResponse;
import com.lyceum.academic.infra.api.dto.ClassroomRequest;
import com.lyceum.academic.infra.api.dto.ClassroomStatusRequest;
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
import java.util.Optional;
import java.util.UUID;

/**
 * REST controller for managing Classrooms.
 * Provides endpoints to create, retrieve, update status, and delete classrooms.
 */
@Tag(name = "Classrooms", description = "Cadastro e gerenciamento de turmas — inclui abertura, fechamento e controle de vagas")
@RestController
@RequestMapping("/classrooms")
public class ClassroomController {

    private final ClassroomRepository classroomRepository;
    private final CrudService crudService;

    public ClassroomController(ClassroomRepository classroomRepository, CrudService crudService) {
        this.classroomRepository = classroomRepository;
        this.crudService = crudService;
    }

    @Operation(summary = "Buscar turma por ID", description = "Retorna os dados de uma turma específica incluindo vagas disponíveis e período de matrícula.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Turma encontrada (ou ausente — Optional vazio)",
                    content = @Content(schema = @Schema(implementation = Classroom.class)))
    })
    @GetMapping("/{id}")
    public Optional<Classroom> getClassroom(
            @Parameter(description = "ID da turma", required = true) @PathVariable UUID id) {
        return classroomRepository.findById(id);
    }

    @Operation(summary = "Listar turmas", description = "Retorna todas as turmas cadastradas.")
    @ApiResponse(responseCode = "200", description = "Lista de turmas",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Classroom.class))))
    @GetMapping
    public List<Classroom> listClassrooms() {
        return crudService.listClassrooms();
    }

    @Operation(summary = "Cadastrar turma",
            description = "Cria uma nova turma vinculada a uma disciplina, com limite de vagas e período de matrícula.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Turma criada com sucesso",
                    content = @Content(schema = @Schema(implementation = Classroom.class))),
            @ApiResponse(responseCode = "400", description = "Payload inválido — disciplina ausente ou vagas < 1",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Disciplina não encontrada",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping
    public Classroom createClassroom(@Valid @RequestBody ClassroomRequest classroomRequest) {
        return crudService.createClassroom(classroomRequest);
    }

    public Classroom createClassroom(Classroom classroom) {
        return classroomRepository.save(classroom);
    }

    @Operation(summary = "Alterar status da turma",
            description = "Abre ou fecha a turma. Turmas fechadas não aceitam novas matrículas. Valores aceitos: OPEN, CLOSED.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = Classroom.class))),
            @ApiResponse(responseCode = "400", description = "Status inválido",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Turma não encontrada",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PatchMapping("/{id}/status")
    public Classroom updateStatus(
            @Parameter(description = "ID da turma", required = true) @PathVariable UUID id,
            @Valid @RequestBody ClassroomStatusRequest request) {
        return crudService.updateClassroomStatus(id, request);
    }

    @Operation(summary = "Excluir turma", description = "Remove a turma do sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Turma excluída com sucesso"),
            @ApiResponse(responseCode = "404", description = "Turma não encontrada",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public void delete(
            @Parameter(description = "ID da turma", required = true) @PathVariable UUID id) {
        crudService.deleteClassroom(id);
    }
}
