package com.lyceum.academic.infra.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record DisciplineRequest(@NotBlank String name, @NotNull UUID courseId) {
}
