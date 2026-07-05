package com.lyceum.academic.infra.api.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * DTO for creating a new Enrollment, identifying the student and the target classroom.
 */
public record CreateEnrollmentRequest(@NotNull UUID studentId, @NotNull UUID classroomId) {
}
