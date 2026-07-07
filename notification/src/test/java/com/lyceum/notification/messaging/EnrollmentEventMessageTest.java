package com.lyceum.notification.messaging;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EnrollmentEventMessageTest {

    @Test
    void recordStoresAllFields() {
        UUID eventId = UUID.randomUUID();
        UUID enrollmentId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        UUID classroomId = UUID.randomUUID();
        Instant occurredAt = Instant.now();

        EnrollmentEventMessage msg = new EnrollmentEventMessage(
                eventId, "ENROLLMENT_CREATED", enrollmentId, studentId, classroomId, occurredAt);

        assertEquals(eventId, msg.eventId());
        assertEquals("ENROLLMENT_CREATED", msg.eventType());
        assertEquals(enrollmentId, msg.enrollmentId());
        assertEquals(studentId, msg.studentId());
        assertEquals(classroomId, msg.classroomId());
        assertEquals(occurredAt, msg.occurredAt());
    }

    @Test
    void nullEventIdIsAccepted() {
        EnrollmentEventMessage msg = new EnrollmentEventMessage(
                null, "ENROLLMENT_CREATED", UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), Instant.now());

        assertNull(msg.eventId());
    }

    @Test
    void recordEqualityBasedOnValues() {
        UUID eventId = UUID.randomUUID();
        UUID enrollmentId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        UUID classroomId = UUID.randomUUID();
        Instant occurredAt = Instant.parse("2024-01-01T00:00:00Z");

        EnrollmentEventMessage msg1 = new EnrollmentEventMessage(
                eventId, "ENROLLMENT_CONFIRMED", enrollmentId, studentId, classroomId, occurredAt);
        EnrollmentEventMessage msg2 = new EnrollmentEventMessage(
                eventId, "ENROLLMENT_CONFIRMED", enrollmentId, studentId, classroomId, occurredAt);

        assertEquals(msg1, msg2);
    }
}
