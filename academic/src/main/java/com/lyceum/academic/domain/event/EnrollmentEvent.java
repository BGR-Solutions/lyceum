package com.lyceum.academic.domain.event;

import java.time.Instant;
import java.util.UUID;

public record EnrollmentEvent(
        UUID eventId,
        String eventType,
        UUID enrollmentId,
        UUID studentId,
        UUID classroomId,
        Instant occurredAt
) {
}
