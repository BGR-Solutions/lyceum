package com.lyceum.academic.application.command;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EnrollmentCommandsTest {

    @Test
    void createEnrollmentCommandStoresFields() {
        UUID studentId = UUID.randomUUID();
        UUID classroomId = UUID.randomUUID();

        CreateEnrollmentCommand command = new CreateEnrollmentCommand(studentId, classroomId);

        assertEquals(studentId, command.getStudentId());
        assertEquals(classroomId, command.getClassroomId());
    }

    @Test
    void confirmEnrollmentCommandStoresEnrollmentId() {
        UUID enrollmentId = UUID.randomUUID();

        ConfirmEnrollmentCommand command = new ConfirmEnrollmentCommand(enrollmentId);

        assertEquals(enrollmentId, command.getEnrollmentId());
    }

    @Test
    void cancelEnrollmentCommandStoresEnrollmentId() {
        UUID enrollmentId = UUID.randomUUID();

        CancelEnrollmentCommand command = new CancelEnrollmentCommand(enrollmentId);

        assertEquals(enrollmentId, command.getEnrollmentId());
    }
}
