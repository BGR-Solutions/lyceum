package com.lyceum.academic.application.command;

import java.util.UUID;

/**
 * Command to create a new enrollment for a student in a classroom.
 */
public class CreateEnrollmentCommand {
    private final UUID studentId;
    private final UUID classroomId;

    public CreateEnrollmentCommand(UUID studentId, UUID classroomId) {
        this.studentId = studentId;
        this.classroomId = classroomId;
    }

    public UUID getStudentId() { return studentId; }
    public UUID getClassroomId() { return classroomId; }
}
