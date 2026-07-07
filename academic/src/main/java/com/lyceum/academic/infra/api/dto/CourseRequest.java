package com.lyceum.academic.infra.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO for creating or updating a Course.
 */
@Schema(description = "Payload para cadastro ou atualização de curso")
public record CourseRequest(
        @Schema(description = "Nome do curso", example = "Ciência da Computação")
        @NotBlank String name) {
}
