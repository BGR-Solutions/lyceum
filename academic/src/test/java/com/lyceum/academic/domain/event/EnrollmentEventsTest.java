package com.lyceum.academic.domain.event;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EnrollmentEventsTest {

    @Test
    void enrollmentCreatedStoresDataAndTimestamp() {
        UUID enrollmentId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        UUID classroomId = UUID.randomUUID();
        Instant before = Instant.now();

        EnrollmentCreated event = new EnrollmentCreated(enrollmentId, studentId, classroomId);

        assertEquals(enrollmentId, event.getEnrollmentId());
        assertEquals(studentId, event.getStudentId());
        assertEquals(classroomId, event.getClassroomId());
        assertNotNull(event.getOccurredAt());
        assertTrue(!event.getOccurredAt().isBefore(before));
    }

    @Test
    void enrollmentConfirmedStoresDataAndTimestamp() {
        UUID enrollmentId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        UUID classroomId = UUID.randomUUID();

        EnrollmentConfirmed event = new EnrollmentConfirmed(enrollmentId, studentId, classroomId);

        assertEquals(enrollmentId, event.getEnrollmentId());
        assertEquals(studentId, event.getStudentId());
        assertEquals(classroomId, event.getClassroomId());
        assertNotNull(event.getOccurredAt());
    }

    @Test
    void enrollmentCancelledStoresDataAndTimestamp() {
        UUID enrollmentId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        UUID classroomId = UUID.randomUUID();

        EnrollmentCancelled event = new EnrollmentCancelled(enrollmentId, studentId, classroomId);

        assertEquals(enrollmentId, event.getEnrollmentId());
        assertEquals(studentId, event.getStudentId());
        assertEquals(classroomId, event.getClassroomId());
        assertNotNull(event.getOccurredAt());
    }
}
