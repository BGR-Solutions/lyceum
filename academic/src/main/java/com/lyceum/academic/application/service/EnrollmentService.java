package com.lyceum.academic.application.service;

import com.lyceum.academic.application.command.CancelEnrollmentCommand;
import com.lyceum.academic.application.command.ConfirmEnrollmentCommand;
import com.lyceum.academic.application.command.CreateEnrollmentCommand;
import com.lyceum.academic.application.ports.ClassroomRepository;
import com.lyceum.academic.application.ports.EnrollmentRepository;
import com.lyceum.academic.application.ports.EventPublisher;
import com.lyceum.academic.domain.entity.Classroom;
import com.lyceum.academic.domain.entity.Enrollment;
import com.lyceum.academic.domain.event.EnrollmentCancelled;
import com.lyceum.academic.domain.event.EnrollmentConfirmed;
import com.lyceum.academic.domain.event.EnrollmentCreated;
import com.lyceum.academic.infra.adapters.repository.StudentRepositoryJpa;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final ClassroomRepository classroomRepository;
    private final EventPublisher eventPublisher;
    private final StudentRepositoryJpa studentRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                             ClassroomRepository classroomRepository,
                             EventPublisher eventPublisher,
                             StudentRepositoryJpa studentRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.classroomRepository = classroomRepository;
        this.eventPublisher = eventPublisher;
        this.studentRepository = studentRepository;
    }

    @Transactional
    public Enrollment createEnrollment(CreateEnrollmentCommand command) {
        if (enrollmentRepository.existsByStudentIdAndClassroomId(command.getStudentId(), command.getClassroomId())) {
            throw new IllegalStateException("Student already enrolled in classroom");
        }

        Classroom classroom = classroomRepository.findById(command.getClassroomId())
                .orElseThrow(() -> new IllegalArgumentException("Classroom not found"));

        if (!classroom.isOpenForEnrollment(LocalDate.now())) {
            throw new IllegalStateException("Enrollment period is closed");
        }

        var student = studentRepository.findById(command.getStudentId())
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        Enrollment enrollment = new Enrollment(student, classroom);
        Enrollment saved = enrollmentRepository.save(enrollment);

        eventPublisher.publish(new EnrollmentCreated(
                saved.getId(),
                saved.getStudent().getId(),
                saved.getClassroom().getId()
        ));

        return saved;
    }

    @Transactional
    public Enrollment confirmEnrollment(ConfirmEnrollmentCommand command) {
        Enrollment enrollment = enrollmentRepository.findById(command.getEnrollmentId())
                .orElseThrow(() -> new IllegalArgumentException("Enrollment not found"));

        Classroom lockedClassroom = classroomRepository.findByIdForUpdate(enrollment.getClassroom().getId())
                .orElseThrow(() -> new IllegalArgumentException("Classroom not found"));

        if (!lockedClassroom.isOpenForEnrollment(LocalDate.now())) {
            throw new IllegalStateException("Enrollment period is closed");
        }

        enrollment.confirm();
        Enrollment saved = enrollmentRepository.save(enrollment);

        eventPublisher.publish(new EnrollmentConfirmed(
                saved.getId(),
                saved.getStudent().getId(),
                saved.getClassroom().getId()
        ));

        return saved;
    }

    @Transactional
    public Enrollment cancelEnrollment(CancelEnrollmentCommand command) {
        Enrollment enrollment = enrollmentRepository.findById(command.getEnrollmentId())
                .orElseThrow(() -> new IllegalArgumentException("Enrollment not found"));

        classroomRepository.findByIdForUpdate(enrollment.getClassroom().getId())
                .orElseThrow(() -> new IllegalArgumentException("Classroom not found"));

        enrollment.cancel();
        Enrollment saved = enrollmentRepository.save(enrollment);

        eventPublisher.publish(new EnrollmentCancelled(
                saved.getId(),
                saved.getStudent().getId(),
                saved.getClassroom().getId()
        ));

        return saved;
    }

    @Transactional(readOnly = true)
    public List<Enrollment> findByStudent(UUID studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }

    @Transactional(readOnly = true)
    public List<Enrollment> findByClassroom(UUID classroomId) {
        return enrollmentRepository.findByClassroomId(classroomId);
    }
}
