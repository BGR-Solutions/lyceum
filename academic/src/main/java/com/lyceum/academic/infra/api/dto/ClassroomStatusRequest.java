package com.lyceum.academic.infra.api.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for updating the status of an existing Classroom.
 */
public record ClassroomStatusRequest(@NotBlank String status) {
}
