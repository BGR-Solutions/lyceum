package com.lyceum.academic.domain.entity;

import com.lyceum.academic.domain.enums.EnrollmentStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.UUID;

/**
 * Represents an enrollment of a student in a classroom.
 * Each enrollment has a unique identifier, a student, a classroom, and a status.
 */
@Entity
@Table(name = "enrollments", uniqueConstraints = {
        @UniqueConstraint(name = "uk_enrollment_student_classroom", columnNames = {"student_id", "classroom_id"})
})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Enrollment {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classroom_id", nullable = false)
    private Classroom classroom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnrollmentStatus status;

    protected Enrollment() {
    }

    public Enrollment(Student student, Classroom classroom) {
        this.id = UUID.randomUUID();
        this.student = student;
        this.classroom = classroom;
        this.status = EnrollmentStatus.PENDING;
    }

    public void confirm() {
        if (this.status == EnrollmentStatus.CONFIRMED) {
            return;
        }
        if (!classroom.hasAvailableSeats()) {
            throw new IllegalStateException("No seats available");
        }
        this.status = EnrollmentStatus.CONFIRMED;
        classroom.consumeSeat();
    }

    public void cancel() {
        if (this.status == EnrollmentStatus.CANCELLED) {
            return;
        }
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
