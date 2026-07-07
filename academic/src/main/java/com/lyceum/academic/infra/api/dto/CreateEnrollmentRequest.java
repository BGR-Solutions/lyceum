package com.lyceum.academic.infra.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * DTO for creating a new Enrollment, identifying the student and the target classroom.
 */
@Schema(description = "Payload para criação de matrícula")
public record CreateEnrollmentRequest(
        @Schema(description = "ID do aluno a ser matriculado", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotNull UUID studentId,

        @Schema(description = "ID da turma na qual o aluno será matriculado", example = "660e8400-e29b-41d4-a716-446655440000")
        @NotNull UUID classroomId) {
}
