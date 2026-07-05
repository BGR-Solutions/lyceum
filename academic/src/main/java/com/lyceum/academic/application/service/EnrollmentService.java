package com.lyceum.academic.application.service;

import com.lyceum.academic.application.command.CreateEnrollmentCommand;
import com.lyceum.academic.application.command.ConfirmEnrollmentCommand;
import com.lyceum.academic.application.command.CancelEnrollmentCommand;
import com.lyceum.academic.application.ports.EnrollmentRepository;
import com.lyceum.academic.application.ports.ClassroomRepository;
import com.lyceum.academic.application.ports.EventPublisher;
import com.lyceum.academic.domain.event.EnrollmentCreated;
import com.lyceum.academic.domain.event.EnrollmentConfirmed;
import com.lyceum.academic.domain.entity.Classroom;
import com.lyceum.academic.domain.entity.Enrollment;
import com.lyceum.academic.domain.entity.Student;
import com.lyceum.academic.domain.event.EnrollmentCancelled;

/**
 * Service class to handle enrollment-related operations.
 * This service provides methods to create, confirm, and cancel enrollments.
 */
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final ClassroomRepository classroomRepository;
    private final EventPublisher eventPublisher;

    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                             ClassroomRepository classroomRepository,
                             EventPublisher eventPublisher) {
        this.enrollmentRepository = enrollmentRepository;
        this.classroomRepository = classroomRepository;
        this.eventPublisher = eventPublisher;
    }

    // Caso de uso: criar matrícula
    public Enrollment createEnrollment(CreateEnrollmentCommand command) {
        Classroom classroom = classroomRepository.findById(command.getClassroomId())
                .orElseThrow(() -> new IllegalArgumentException("Classroom not found"));

        if (!classroom.isOpenForEnrollment(java.time.LocalDate.now())) {
            throw new IllegalStateException("Enrollment period is closed");
        }

        Student student = new Student(command.getStudentId(), "Unknown"); // Nome pode vir de outro contexto

        Enrollment enrollment = new Enrollment(student, classroom);
        Enrollment saved = enrollmentRepository.save(enrollment);

        // Dispara evento de domínio
        eventPublisher.publish(new EnrollmentCreated(
                saved.getId(),
                saved.getStudent().getId(),
                saved.getClassroom().getId()
        ));

        return saved;
    }

    // Caso de uso: confirmar matrícula
    public Enrollment confirmEnrollment(ConfirmEnrollmentCommand command) {
        Enrollment enrollment = enrollmentRepository.findById(command.getEnrollmentId())
                .orElseThrow(() -> new IllegalArgumentException("Enrollment not found"));

        enrollment.confirm();
        Enrollment saved = enrollmentRepository.save(enrollment);

        eventPublisher.publish(new EnrollmentConfirmed(
                saved.getId(),
                saved.getStudent().getId(),
                saved.getClassroom().getId()
        ));

        return saved;
    }

    // Caso de uso: cancelar matrícula
    public Enrollment cancelEnrollment(CancelEnrollmentCommand command) {
        Enrollment enrollment = enrollmentRepository.findById(command.getEnrollmentId())
                .orElseThrow(() -> new IllegalArgumentException("Enrollment not found"));

        enrollment.cancel();
        Enrollment saved = enrollmentRepository.save(enrollment);

        eventPublisher.publish(new EnrollmentCancelled(
                saved.getId(),
                saved.getStudent().getId(),
                saved.getClassroom().getId()
        ));

        return saved;
    }
}
