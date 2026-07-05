package com.lyceum.notification.messaging;

import java.time.Instant;
import java.util.UUID;

public record EnrollmentEventMessage(
        UUID eventId,
        String eventType,
        UUID enrollmentId,
        UUID studentId,
        UUID classroomId,
        Instant occurredAt
) {
}
