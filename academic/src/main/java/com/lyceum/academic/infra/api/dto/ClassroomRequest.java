package com.lyceum.academic.infra.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO for creating a new Classroom, carrying the discipline, seat configuration, and enrollment period.
 */
@Schema(description = "Payload para cadastro de turma")
public record ClassroomRequest(
        @Schema(description = "ID da disciplina associada à turma", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotNull UUID disciplineId,

        @Schema(description = "Número máximo de vagas (mínimo 1)", example = "30")
        @Min(1) int maxSeats,

        @Schema(description = "Data de início do período de matrícula", example = "2025-01-15")
        @NotNull LocalDate enrollmentStart,

        @Schema(description = "Data de encerramento do período de matrícula", example = "2025-07-15")
        @NotNull LocalDate enrollmentEnd
) {
}
