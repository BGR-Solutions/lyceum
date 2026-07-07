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
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Service class to handle enrollment-related operations.
 * This service provides methods to create, confirm, and cancel enrollments.
 */
@Service
public class EnrollmentService {

    private static final Logger log = LoggerFactory.getLogger(EnrollmentService.class);

    private final EnrollmentRepository enrollmentRepository;
    private final ClassroomRepository classroomRepository;
    private final EventPublisher eventPublisher;
    private final StudentRepositoryJpa studentRepository;

    private final Counter enrollmentsCreated;
    private final Counter enrollmentsConfirmed;
    private final Counter enrollmentsCancelled;
    private final Counter enrollmentsRejectedNoSeats;

    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                             ClassroomRepository classroomRepository,
                             EventPublisher eventPublisher,
                             StudentRepositoryJpa studentRepository,
                             MeterRegistry meterRegistry) {
        this.enrollmentRepository = enrollmentRepository;
        this.classroomRepository = classroomRepository;
        this.eventPublisher = eventPublisher;
        this.studentRepository = studentRepository;

        this.enrollmentsCreated      = meterRegistry.counter("enrollment.created");
        this.enrollmentsConfirmed    = meterRegistry.counter("enrollment.confirmed");
        this.enrollmentsCancelled    = meterRegistry.counter("enrollment.cancelled");
        this.enrollmentsRejectedNoSeats = meterRegistry.counter("enrollment.rejected.no_seats");
    }

    @Transactional
    public Enrollment createEnrollment(CreateEnrollmentCommand command) {
        if (enrollmentRepository.existsByStudentIdAndClassroomIdAndStatusNot(command.getStudentId(), command.getClassroomId(), com.lyceum.academic.domain.enums.EnrollmentStatus.CANCELLED)) {
            throw new IllegalStateException("Student already enrolled in classroom");
        }

        Classroom classroom = classroomRepository.findByIdForUpdate(command.getClassroomId())
                .orElseThrow(() -> new IllegalArgumentException("Classroom not found"));

        if (!classroom.isOpenForEnrollment(LocalDate.now())) {
            throw new IllegalStateException("Enrollment period is closed");
        }

        if (!classroom.hasAvailableSeats()) {
            enrollmentsRejectedNoSeats.increment();
            throw new IllegalStateException("No seats available");
        }

        var student = studentRepository.findById(command.getStudentId())
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        Enrollment enrollment = new Enrollment(student, classroom);
        enrollment.confirm();
        Enrollment saved = enrollmentRepository.save(enrollment);

        eventPublisher.publish(new EnrollmentConfirmed(
                saved.getId(),
                saved.getStudent().getId(),
                saved.getClassroom().getId()
        ));

        enrollmentsCreated.increment();
        enrollmentsConfirmed.increment();
        log.info("Enrollment created and confirmed: enrollmentId={} studentId={} classroomId={} occupiedSeats={}/{}",
                saved.getId(), saved.getStudent().getId(), saved.getClassroom().getId(),
                classroom.getSeatLimit().getOccupiedSeats(),
                classroom.getSeatLimit().getMaxSeats());

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

        try {
            enrollment.confirm();
        } catch (IllegalStateException ex) {
            enrollmentsRejectedNoSeats.increment();
            log.warn("Enrollment confirmation rejected — no seats available: enrollmentId={} classroomId={}",
                    enrollment.getId(), lockedClassroom.getId());
            throw ex;
        }

        Enrollment saved = enrollmentRepository.save(enrollment);

        eventPublisher.publish(new EnrollmentConfirmed(
                saved.getId(),
                saved.getStudent().getId(),
                saved.getClassroom().getId()
        ));

        enrollmentsConfirmed.increment();
        log.info("Enrollment confirmed: enrollmentId={} studentId={} classroomId={} occupiedSeats={}/{}",
                saved.getId(), saved.getStudent().getId(), saved.getClassroom().getId(),
                lockedClassroom.getSeatLimit().getOccupiedSeats(),
                lockedClassroom.getSeatLimit().getMaxSeats());

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

        enrollmentsCancelled.increment();
        log.info("Enrollment cancelled: enrollmentId={} studentId={} classroomId={}",
                saved.getId(), saved.getStudent().getId(), saved.getClassroom().getId());

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
