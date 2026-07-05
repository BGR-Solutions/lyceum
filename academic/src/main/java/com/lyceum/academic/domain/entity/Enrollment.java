package com.lyceum.academic.domain.entity;

import com.lyceum.academic.domain.enums.EnrollmentStatus;

import java.util.UUID;

/**
 * Represents an enrollment of a student in a classroom.
 * Each enrollment has a unique identifier, a student, a classroom, and a status.
 */
public class Enrollment {
    private final UUID id;
    private final Student student;
    private final Classroom classroom;
    private EnrollmentStatus status;

    public Enrollment(Student student, Classroom classroom) {
        this.id = UUID.randomUUID();
        this.student = student;
        this.classroom = classroom;
        this.status = EnrollmentStatus.PENDING;
    }

    public void confirm() {
        if (!classroom.hasAvailableSeats()) {
            throw new IllegalStateException("No seats available");
        }
        this.status = EnrollmentStatus.CONFIRMED;
        classroom.consumeSeat();
    }

    public void cancel() {
        if (this.status == EnrollmentStatus.CONFIRMED) {
            classroom.releaseSeat();
        }
        this.status = EnrollmentStatus.CANCELLED;
    }

    public UUID getId() { return id; }
    public Student getStudent() { return student; }
    public Classroom getClassroom() { return classroom; }
    public EnrollmentStatus getStatus() { return status; }
}
