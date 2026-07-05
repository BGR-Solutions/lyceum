package com.lyceum.academic.infra.api.dto;

import jakarta.validation.constraints.NotBlank;

public record ClassroomStatusRequest(@NotBlank String status) {
}
