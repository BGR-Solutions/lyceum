package com.lyceum.academic.domain.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Immutable record representing a serializable enrollment domain event payload.
 * Used to transport enrollment event data across module boundaries (e.g., via RabbitMQ).
 */
public record EnrollmentEvent(
        UUID eventId,
        String eventType,
        UUID enrollmentId,
        UUID studentId,
        UUID classroomId,
        Instant occurredAt
) {
}
