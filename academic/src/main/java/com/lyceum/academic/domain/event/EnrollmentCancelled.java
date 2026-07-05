package com.lyceum.academic.domain.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents the event of an enrollment being cancelled.
 * This event captures the unique identifiers of the enrollment, student, and classroom involved,
 * along with the timestamp of when the event occurred.
 */
public class EnrollmentCancelled {
    private final UUID enrollmentId;
    private final UUID studentId;
    private final UUID classroomId;
    private final Instant occurredAt;

    public EnrollmentCancelled(UUID enrollmentId, UUID studentId, UUID classroomId) {
        this.enrollmentId = enrollmentId;
        this.studentId = studentId;
        this.classroomId = classroomId;
        this.occurredAt = Instant.now();
    }

    public UUID getEnrollmentId() { return enrollmentId; }
    public UUID getStudentId() { return studentId; }
    public UUID getClassroomId() { return classroomId; }
    public Instant getOccurredAt() { return occurredAt; }
}
