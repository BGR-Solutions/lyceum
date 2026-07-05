package com.lyceum.academic.infra.api.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for creating or updating a Student.
 */
public record StudentRequest(@NotBlank String name) {
}
