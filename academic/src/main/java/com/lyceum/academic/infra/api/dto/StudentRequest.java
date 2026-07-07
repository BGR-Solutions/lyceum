package com.lyceum.academic.infra.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO for creating or updating a Student.
 */
@Schema(description = "Payload para cadastro ou atualização de aluno")
public record StudentRequest(
        @Schema(description = "Nome completo do aluno", example = "Ana Souza")
        @NotBlank String name) {
}
