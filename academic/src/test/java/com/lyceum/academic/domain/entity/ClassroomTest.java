package com.lyceum.academic.domain.entity;

import com.lyceum.academic.domain.enums.ClassroomStatus;
import com.lyceum.academic.domain.valueobject.EnrollmentPeriod;
import com.lyceum.academic.domain.valueobject.SeatLimit;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClassroomTest {

    @Test
    void classroomStartsOpenAndExposesGetters() {
        UUID classroomId = UUID.randomUUID();
        Discipline discipline = new Discipline(UUID.randomUUID(), "Math");
        Classroom classroom = new Classroom(
                classroomId,
                discipline,
                new SeatLimit(2),
                new EnrollmentPeriod(LocalDate.now().minusDays(1), LocalDate.now().plusDays(1))
        );

        assertEquals(classroomId, classroom.getId());
        assertEquals(discipline, classroom.getDiscipline());
        assertEquals(ClassroomStatus.OPEN, classroom.getStatus());
    }

    @Test
    void isOpenForEnrollmentDependsOnPeriodAndStatus() {
        LocalDate today = LocalDate.now();
        Classroom classroom = new Classroom(
                UUID.randomUUID(),
                new Discipline(UUID.randomUUID(), "Science"),
                new SeatLimit(1),
                new EnrollmentPeriod(today.minusDays(1), today.plusDays(1))
        );

        assertTrue(classroom.isOpenForEnrollment(today));
        assertFalse(classroom.isOpenForEnrollment(today.plusDays(10)));
    }

    @Test
    void seatOperationsAffectAvailability() {
        Classroom classroom = new Classroom(
                UUID.randomUUID(),
                new Discipline(UUID.randomUUID(), "Physics"),
                new SeatLimit(1),
                new EnrollmentPeriod(LocalDate.now().minusDays(1), LocalDate.now().plusDays(1))
        );

        assertTrue(classroom.hasAvailableSeats());
        classroom.consumeSeat();
        assertFalse(classroom.hasAvailableSeats());
        classroom.releaseSeat();
        assertTrue(classroom.hasAvailableSeats());
    }
}
