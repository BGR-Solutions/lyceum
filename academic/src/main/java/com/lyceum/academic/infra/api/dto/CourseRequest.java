package com.lyceum.academic.infra.api.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for creating or updating a Course.
 */
public record CourseRequest(@NotBlank String name) {
}
