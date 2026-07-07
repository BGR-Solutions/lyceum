package com.lyceum.academic.infra.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * DTO for creating or updating a Discipline, including its name and the associated course.
 */
@Schema(description = "Payload para cadastro ou atualização de disciplina")
public record DisciplineRequest(
        @Schema(description = "Nome da disciplina", example = "Estrutura de Dados")
        @NotBlank String name,

        @Schema(description = "ID do curso ao qual a disciplina pertence", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotNull UUID courseId) {
}
