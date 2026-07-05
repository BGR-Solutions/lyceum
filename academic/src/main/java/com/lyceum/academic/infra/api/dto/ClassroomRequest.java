package com.lyceum.academic.infra.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record ClassroomRequest(
        @NotNull UUID disciplineId,
        @Min(1) int maxSeats,
        @NotNull LocalDate enrollmentStart,
        @NotNull LocalDate enrollmentEnd
) {
}
