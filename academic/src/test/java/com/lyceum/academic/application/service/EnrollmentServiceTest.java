package com.lyceum.academic.application.service;

import com.lyceum.academic.application.command.CancelEnrollmentCommand;
import com.lyceum.academic.application.command.ConfirmEnrollmentCommand;
import com.lyceum.academic.application.command.CreateEnrollmentCommand;
import com.lyceum.academic.application.ports.ClassroomRepository;
import com.lyceum.academic.application.ports.EnrollmentRepository;
import com.lyceum.academic.application.ports.EventPublisher;
import com.lyceum.academic.domain.entity.Classroom;
import com.lyceum.academic.domain.entity.Discipline;
import com.lyceum.academic.domain.entity.Enrollment;
import com.lyceum.academic.domain.entity.Student;
import com.lyceum.academic.domain.enums.EnrollmentStatus;
import com.lyceum.academic.domain.valueobject.EnrollmentPeriod;
import com.lyceum.academic.domain.valueobject.SeatLimit;
import com.lyceum.academic.infra.adapters.repository.StudentRepositoryJpa;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;
    @Mock
    private ClassroomRepository classroomRepository;
    @Mock
    private EventPublisher eventPublisher;
    @Mock
    private StudentRepositoryJpa studentRepository;

    private EnrollmentService service;

    @BeforeEach
    void setUp() {
        service = new EnrollmentService(enrollmentRepository, classroomRepository, eventPublisher, studentRepository,
                new SimpleMeterRegistry());
    }

    @Test
    void createEnrollmentThrowsWhenClassroomNotFound() {
        UUID classroomId = UUID.randomUUID();
        when(classroomRepository.findByIdForUpdate(classroomId)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.createEnrollment(new CreateEnrollmentCommand(UUID.randomUUID(), classroomId)));

        assertEquals("Classroom not found", ex.getMessage());
    }

    @Test
    void createEnrollmentThrowsWhenClassroomClosed() {
        Classroom classroom = new Classroom(
                UUID.randomUUID(),
                new Discipline(UUID.randomUUID(), "Math"),
                new SeatLimit(1),
                new EnrollmentPeriod(LocalDate.now().minusDays(10), LocalDate.now().minusDays(1))
        );
        when(classroomRepository.findByIdForUpdate(classroom.getId())).thenReturn(Optional.of(classroom));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> service.createEnrollment(new CreateEnrollmentCommand(UUID.randomUUID(), classroom.getId())));

        assertEquals("Enrollment period is closed", ex.getMessage());
    }

    @Test
    void createEnrollmentPersistsAndPublishesEvent() {
        UUID studentId = UUID.randomUUID();
        Classroom classroom = new Classroom(
                UUID.randomUUID(),
                new Discipline(UUID.randomUUID(), "Math"),
                new SeatLimit(1),
                new EnrollmentPeriod(LocalDate.now().minusDays(1), LocalDate.now().plusDays(1))
        );
        when(classroomRepository.findByIdForUpdate(classroom.getId())).thenReturn(Optional.of(classroom));
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(new Student(studentId, "Alice")));
        when(enrollmentRepository.save(any(Enrollment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(eventPublisher).publish(any(com.lyceum.academic.domain.event.EnrollmentConfirmed.class));

        Enrollment created = service.createEnrollment(new CreateEnrollmentCommand(studentId, classroom.getId()));

        assertEquals(studentId, created.getStudent().getId());
        assertEquals("Alice", created.getStudent().getName());
        assertEquals(EnrollmentStatus.CONFIRMED, created.getStatus());
        verify(enrollmentRepository).save(any(Enrollment.class));
        verify(eventPublisher).publish(any(com.lyceum.academic.domain.event.EnrollmentConfirmed.class));
    }

    @Test
    void confirmEnrollmentThrowsWhenEnrollmentNotFound() {
        UUID enrollmentId = UUID.randomUUID();
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.confirmEnrollment(new ConfirmEnrollmentCommand(enrollmentId)));

        assertEquals("Enrollment not found", ex.getMessage());
    }

    @Test
    void confirmEnrollmentConfirmsEnrollmentPersistsAndPublishesEvent() {
        Enrollment enrollment = new Enrollment(new Student(UUID.randomUUID(), "Alice"), buildClassroom(1));
        when(enrollmentRepository.findById(enrollment.getId())).thenReturn(Optional.of(enrollment));
        when(classroomRepository.findByIdForUpdate(enrollment.getClassroom().getId())).thenReturn(Optional.of(enrollment.getClassroom()));
        when(enrollmentRepository.save(enrollment)).thenReturn(enrollment);
        doNothing().when(eventPublisher).publish(any(com.lyceum.academic.domain.event.EnrollmentConfirmed.class));

        Enrollment saved = service.confirmEnrollment(new ConfirmEnrollmentCommand(enrollment.getId()));

        assertEquals(EnrollmentStatus.CONFIRMED, saved.getStatus());
        verify(enrollmentRepository).save(enrollment);
        verify(eventPublisher).publish(any(com.lyceum.academic.domain.event.EnrollmentConfirmed.class));
    }

    @Test
    void cancelEnrollmentThrowsWhenEnrollmentNotFound() {
        UUID enrollmentId = UUID.randomUUID();
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.cancelEnrollment(new CancelEnrollmentCommand(enrollmentId)));

        assertEquals("Enrollment not found", ex.getMessage());
    }

    @Test
    void cancelEnrollmentCancelsEnrollmentPersistsAndPublishesEvent() {
        Enrollment enrollment = new Enrollment(new Student(UUID.randomUUID(), "Alice"), buildClassroom(1));
        when(enrollmentRepository.findById(enrollment.getId())).thenReturn(Optional.of(enrollment));
        when(classroomRepository.findByIdForUpdate(enrollment.getClassroom().getId())).thenReturn(Optional.of(enrollment.getClassroom()));
        when(enrollmentRepository.save(enrollment)).thenReturn(enrollment);
        doNothing().when(eventPublisher).publish(any(com.lyceum.academic.domain.event.EnrollmentCancelled.class));

        Enrollment saved = service.cancelEnrollment(new CancelEnrollmentCommand(enrollment.getId()));

        assertEquals(EnrollmentStatus.CANCELLED, saved.getStatus());
        verify(enrollmentRepository).save(enrollment);
        verify(eventPublisher).publish(any(com.lyceum.academic.domain.event.EnrollmentCancelled.class));
    }

    // --- Regra: um aluno não pode se matricular duas vezes na mesma turma ---

    @Test
    void createEnrollmentThrowsWhenStudentAlreadyEnrolled() {
        UUID studentId = UUID.randomUUID();
        UUID classroomId = UUID.randomUUID();
        when(enrollmentRepository.existsByStudentIdAndClassroomId(studentId, classroomId)).thenReturn(true);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> service.createEnrollment(new CreateEnrollmentCommand(studentId, classroomId)));

        assertEquals("Student already enrolled in classroom", ex.getMessage());
    }

    // --- Regra: aluno deve existir para ser matriculado ---

    @Test
    void createEnrollmentThrowsWhenStudentNotFound() {
        UUID studentId = UUID.randomUUID();
        Classroom classroom = buildClassroom(5);
        when(enrollmentRepository.existsByStudentIdAndClassroomId(studentId, classroom.getId())).thenReturn(false);
        when(classroomRepository.findByIdForUpdate(classroom.getId())).thenReturn(Optional.of(classroom));
        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.createEnrollment(new CreateEnrollmentCommand(studentId, classroom.getId())));

        assertEquals("Student not found", ex.getMessage());
    }

    @Test
    void createEnrollmentThrowsWhenNoSeatsAvailable() {
        UUID studentId = UUID.randomUUID();
        Classroom fullClassroom = buildClassroom(1);
        fullClassroom.consumeSeat();
        when(classroomRepository.findByIdForUpdate(fullClassroom.getId())).thenReturn(Optional.of(fullClassroom));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> service.createEnrollment(new CreateEnrollmentCommand(studentId, fullClassroom.getId())));

        assertEquals("No seats available", ex.getMessage());
    }

    // --- Regra: aluno só pode ser matriculado em turmas abertas (no momento da confirmação) ---

    @Test
    void confirmEnrollmentThrowsWhenClassroomPeriodClosed() {
        Classroom openClassroom = buildClassroom(5);
        Classroom closedClassroom = new Classroom(
                openClassroom.getId(),
                new Discipline(UUID.randomUUID(), "Math"),
                new SeatLimit(5),
                new EnrollmentPeriod(LocalDate.now().minusDays(10), LocalDate.now().minusDays(1))
        );
        Enrollment enrollment = new Enrollment(new Student(UUID.randomUUID(), "Alice"), openClassroom);
        when(enrollmentRepository.findById(enrollment.getId())).thenReturn(Optional.of(enrollment));
        when(classroomRepository.findByIdForUpdate(enrollment.getClassroom().getId())).thenReturn(Optional.of(closedClassroom));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> service.confirmEnrollment(new ConfirmEnrollmentCommand(enrollment.getId())));

        assertEquals("Enrollment period is closed", ex.getMessage());
    }

    // --- Regra: turma possui limite de vagas ---

    @Test
    void confirmEnrollmentThrowsWhenNoSeatsAvailable() {
        Classroom fullClassroom = buildClassroom(1);
        fullClassroom.consumeSeat();
        Enrollment enrollment = new Enrollment(new Student(UUID.randomUUID(), "Alice"), fullClassroom);
        when(enrollmentRepository.findById(enrollment.getId())).thenReturn(Optional.of(enrollment));
        when(classroomRepository.findByIdForUpdate(fullClassroom.getId())).thenReturn(Optional.of(fullClassroom));

        assertThrows(IllegalStateException.class,
                () -> service.confirmEnrollment(new ConfirmEnrollmentCommand(enrollment.getId())));
    }

    // --- Regra: ao confirmar, a vaga da turma deve ser consumida ---

    @Test
    void confirmEnrollmentConsumesClassroomSeat() {
        Classroom classroom = buildClassroom(1);
        Enrollment enrollment = new Enrollment(new Student(UUID.randomUUID(), "Alice"), classroom);
        when(enrollmentRepository.findById(enrollment.getId())).thenReturn(Optional.of(enrollment));
        when(classroomRepository.findByIdForUpdate(classroom.getId())).thenReturn(Optional.of(classroom));
        when(enrollmentRepository.save(enrollment)).thenReturn(enrollment);
        doNothing().when(eventPublisher).publish(any(com.lyceum.academic.domain.event.EnrollmentConfirmed.class));

        service.confirmEnrollment(new ConfirmEnrollmentCommand(enrollment.getId()));

        assertFalse(classroom.hasAvailableSeats());
        assertEquals(EnrollmentStatus.CONFIRMED, enrollment.getStatus());
    }

    // --- Regra: ao cancelar matrícula confirmada, a vaga deve ser liberada ---

    @Test
    void cancelConfirmedEnrollmentReleasesSeat() {
        Classroom classroom = buildClassroom(1);
        Enrollment enrollment = new Enrollment(new Student(UUID.randomUUID(), "Alice"), classroom);
        enrollment.confirm();
        when(enrollmentRepository.findById(enrollment.getId())).thenReturn(Optional.of(enrollment));
        when(classroomRepository.findByIdForUpdate(classroom.getId())).thenReturn(Optional.of(classroom));
        when(enrollmentRepository.save(enrollment)).thenReturn(enrollment);
        doNothing().when(eventPublisher).publish(any(com.lyceum.academic.domain.event.EnrollmentCancelled.class));

        service.cancelEnrollment(new CancelEnrollmentCommand(enrollment.getId()));

        assertTrue(classroom.hasAvailableSeats());
        assertEquals(EnrollmentStatus.CANCELLED, enrollment.getStatus());
    }

    // --- Regra: consulta de matrículas por aluno ---

    @Test
    void findByStudentDelegatesToRepository() {
        UUID studentId = UUID.randomUUID();
        List<Enrollment> expected = List.of(new Enrollment(new Student(studentId, "Alice"), buildClassroom(1)));
        when(enrollmentRepository.findByStudentId(studentId)).thenReturn(expected);

        List<Enrollment> result = service.findByStudent(studentId);

        assertEquals(expected, result);
        verify(enrollmentRepository).findByStudentId(studentId);
    }

    // --- Regra: consulta de matrículas por turma ---

    @Test
    void findByClassroomDelegatesToRepository() {
        UUID classroomId = UUID.randomUUID();
        List<Enrollment> expected = List.of(new Enrollment(new Student(UUID.randomUUID(), "Bob"), buildClassroom(1)));
        when(enrollmentRepository.findByClassroomId(classroomId)).thenReturn(expected);

        List<Enrollment> result = service.findByClassroom(classroomId);

        assertEquals(expected, result);
        verify(enrollmentRepository).findByClassroomId(classroomId);
    }

    private static Classroom buildClassroom(int seats) {
        return new Classroom(
                UUID.randomUUID(),
                new Discipline(UUID.randomUUID(), "Math"),
                new SeatLimit(seats),
                new EnrollmentPeriod(LocalDate.now().minusDays(1), LocalDate.now().plusDays(1))
        );
    }
}
