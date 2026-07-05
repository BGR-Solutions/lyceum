package com.lyceum.academic.infra.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * DTO for creating or updating a Discipline, including its name and the associated course.
 */
public record DisciplineRequest(@NotBlank String name, @NotNull UUID courseId) {
}
