package com.lyceum.academic.infra.api;

import com.lyceum.academic.application.command.CancelEnrollmentCommand;
import com.lyceum.academic.application.command.ConfirmEnrollmentCommand;
import com.lyceum.academic.application.command.CreateEnrollmentCommand;
import com.lyceum.academic.application.service.EnrollmentService;
import com.lyceum.academic.domain.entity.Enrollment;
import com.lyceum.academic.infra.api.dto.ApiErrorResponse;
import com.lyceum.academic.infra.api.dto.CreateEnrollmentRequest;
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
 * REST controller for managing Enrollments.
 * Provides endpoints to create, confirm, cancel, and query enrollments.
 */
@Tag(name = "Enrollments", description = "Gerenciamento de matrículas — criação, confirmação, cancelamento e consultas por aluno ou turma")
@RestController
@RequestMapping("/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @Operation(summary = "Matricular aluno em turma",
            description = "Cria uma matrícula com status PENDENTE para o aluno na turma indicada. " +
                    "A turma deve estar aberta e o aluno não pode já estar matriculado nela.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Matrícula criada com status PENDENTE",
                    content = @Content(schema = @Schema(implementation = Enrollment.class))),
            @ApiResponse(responseCode = "400", description = "Payload inválido",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Aluno ou turma não encontrados",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Turma fechada, sem vagas ou aluno já matriculado",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping
    public Enrollment createEnrollment(@Valid @RequestBody CreateEnrollmentRequest request) {
        return enrollmentService.createEnrollment(
                new CreateEnrollmentCommand(request.studentId(), request.classroomId())
        );
    }

    public Enrollment createEnrollment(UUID studentId, UUID classroomId) {
        return enrollmentService.createEnrollment(
                new CreateEnrollmentCommand(studentId, classroomId)
        );
    }

    @Operation(summary = "Confirmar matrícula",
            description = "Altera o status da matrícula de PENDENTE para CONFIRMADA e consome uma vaga da turma.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Matrícula confirmada com sucesso",
                    content = @Content(schema = @Schema(implementation = Enrollment.class))),
            @ApiResponse(responseCode = "404", description = "Matrícula não encontrada",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Matrícula não está no status PENDENTE ou sem vagas disponíveis",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/{id}/confirm")
    public Enrollment confirmEnrollment(
            @Parameter(description = "ID da matrícula", required = true) @PathVariable UUID id) {
        return enrollmentService.confirmEnrollment(new ConfirmEnrollmentCommand(id));
    }

    @Operation(summary = "Cancelar matrícula",
            description = "Cancela a matrícula. Se estava CONFIRMADA, a vaga da turma é liberada.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Matrícula cancelada com sucesso",
                    content = @Content(schema = @Schema(implementation = Enrollment.class))),
            @ApiResponse(responseCode = "404", description = "Matrícula não encontrada",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Matrícula já está cancelada",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/{id}/cancel")
    public Enrollment cancelEnrollment(
            @Parameter(description = "ID da matrícula", required = true) @PathVariable UUID id) {
        return enrollmentService.cancelEnrollment(new CancelEnrollmentCommand(id));
    }

    @Operation(summary = "Consultar matrículas por aluno",
            description = "Retorna todas as matrículas de um aluno, independente do status.")
    @ApiResponse(responseCode = "200", description = "Lista de matrículas do aluno",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Enrollment.class))))
    @GetMapping("/by-student/{studentId}")
    public List<Enrollment> getByStudent(
            @Parameter(description = "ID do aluno", required = true) @PathVariable UUID studentId) {
        return enrollmentService.findByStudent(studentId);
    }

    @Operation(summary = "Consultar matrículas por turma",
            description = "Retorna todas as matrículas de uma turma, independente do status.")
    @ApiResponse(responseCode = "200", description = "Lista de matrículas da turma",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Enrollment.class))))
    @GetMapping("/by-classroom/{classroomId}")
    public List<Enrollment> getByClassroom(
            @Parameter(description = "ID da turma", required = true) @PathVariable UUID classroomId) {
        return enrollmentService.findByClassroom(classroomId);
    }
}
