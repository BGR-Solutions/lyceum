package com.lyceum.academic.infra.api;

import com.lyceum.academic.application.service.EnrollmentService;
import com.lyceum.academic.domain.entity.Classroom;
import com.lyceum.academic.domain.entity.Discipline;
import com.lyceum.academic.domain.entity.Enrollment;
import com.lyceum.academic.domain.entity.Student;
import com.lyceum.academic.domain.valueobject.EnrollmentPeriod;
import com.lyceum.academic.domain.valueobject.SeatLimit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnrollmentControllerTest {

    @Mock
    private EnrollmentService enrollmentService;

    @InjectMocks
    private EnrollmentController controller;

    @Test
    void createEnrollmentDelegatesToService() {
        UUID studentId = UUID.randomUUID();
        UUID classroomId = UUID.randomUUID();
        Enrollment expected = buildEnrollment();
        when(enrollmentService.createEnrollment(org.mockito.ArgumentMatchers.any())).thenReturn(expected);

        Enrollment result = controller.createEnrollment(studentId, classroomId);

        assertEquals(expected, result);
        ArgumentCaptor<com.lyceum.academic.application.command.CreateEnrollmentCommand> captor = ArgumentCaptor.forClass(com.lyceum.academic.application.command.CreateEnrollmentCommand.class);
        verify(enrollmentService).createEnrollment(captor.capture());
        assertEquals(studentId, captor.getValue().getStudentId());
        assertEquals(classroomId, captor.getValue().getClassroomId());
    }

    @Test
    void confirmEnrollmentDelegatesToService() {
        UUID enrollmentId = UUID.randomUUID();
        Enrollment expected = buildEnrollment();
        when(enrollmentService.confirmEnrollment(org.mockito.ArgumentMatchers.any())).thenReturn(expected);

        Enrollment result = controller.confirmEnrollment(enrollmentId);

        assertEquals(expected, result);
        ArgumentCaptor<com.lyceum.academic.application.command.ConfirmEnrollmentCommand> captor = ArgumentCaptor.forClass(com.lyceum.academic.application.command.ConfirmEnrollmentCommand.class);
        verify(enrollmentService).confirmEnrollment(captor.capture());
        assertEquals(enrollmentId, captor.getValue().getEnrollmentId());
    }

    @Test
    void cancelEnrollmentDelegatesToService() {
        UUID enrollmentId = UUID.randomUUID();
        Enrollment expected = buildEnrollment();
        when(enrollmentService.cancelEnrollment(org.mockito.ArgumentMatchers.any())).thenReturn(expected);

        Enrollment result = controller.cancelEnrollment(enrollmentId);

        assertEquals(expected, result);
        ArgumentCaptor<com.lyceum.academic.application.command.CancelEnrollmentCommand> captor = ArgumentCaptor.forClass(com.lyceum.academic.application.command.CancelEnrollmentCommand.class);
        verify(enrollmentService).cancelEnrollment(captor.capture());
        assertEquals(enrollmentId, captor.getValue().getEnrollmentId());
    }

    private static Enrollment buildEnrollment() {
        Classroom classroom = new Classroom(
                UUID.randomUUID(),
                new Discipline(UUID.randomUUID(), "Math"),
                new SeatLimit(5),
                new EnrollmentPeriod(LocalDate.now().minusDays(1), LocalDate.now().plusDays(1))
        );
        return new Enrollment(new Student(UUID.randomUUID(), "Alice"), classroom);
    }
}
