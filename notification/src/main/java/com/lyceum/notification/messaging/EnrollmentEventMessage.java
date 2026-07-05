package com.lyceum.notification.messaging;

import java.time.Instant;
import java.util.UUID;

/**
 * Immutable record representing a deserialized enrollment event message received from RabbitMQ.
 * Maps to the payload produced by the academic module's {@code EnrollmentEvent}.
 */
public record EnrollmentEventMessage(
        UUID eventId,
        String eventType,
        UUID enrollmentId,
        UUID studentId,
        UUID classroomId,
        Instant occurredAt
) {
}
