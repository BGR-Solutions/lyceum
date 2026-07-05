package com.lyceum.academic.infra.api.dto;

import jakarta.validation.constraints.NotBlank;

public record CourseRequest(@NotBlank String name) {
}
