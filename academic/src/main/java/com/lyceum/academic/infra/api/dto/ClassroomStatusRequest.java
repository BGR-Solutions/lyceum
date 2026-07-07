package com.lyceum.academic.infra.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO for updating the status of an existing Classroom.
 */
@Schema(description = "Payload para alteração de status da turma")
public record ClassroomStatusRequest(
        @Schema(description = "Novo status da turma", example = "OPEN", allowableValues = {"OPEN", "CLOSED"})
        @NotBlank String status) {
}
