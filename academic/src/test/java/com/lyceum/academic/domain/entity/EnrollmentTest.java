package com.lyceum.academic.domain.entity;

import com.lyceum.academic.domain.enums.EnrollmentStatus;
import com.lyceum.academic.domain.valueobject.EnrollmentPeriod;
import com.lyceum.academic.domain.valueobject.SeatLimit;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EnrollmentTest {

    @Test
    void enrollmentStartsPending() {
        Enrollment enrollment = new Enrollment(buildStudent(), buildClassroom(2));

        assertNotNull(enrollment.getId());
        assertEquals(EnrollmentStatus.PENDING, enrollment.getStatus());
    }

    @Test
    void confirmChangesStatusAndConsumesSeat() {
        Classroom classroom = buildClassroom(1);
        Enrollment enrollment = new Enrollment(buildStudent(), classroom);

        enrollment.confirm();

        assertEquals(EnrollmentStatus.CONFIRMED, enrollment.getStatus());
        IllegalStateException ex = assertThrows(IllegalStateException.class, classroom::consumeSeat);
        assertEquals("No seats available", ex.getMessage());
    }

    @Test
    void confirmThrowsWhenNoSeatAvailable() {
        Classroom classroom = buildClassroomWithNoAvailableSeats();
        Enrollment enrollment = new Enrollment(buildStudent(), classroom);

        IllegalStateException ex = assertThrows(IllegalStateException.class, enrollment::confirm);

        assertEquals("No seats available", ex.getMessage());
    }

    @Test
    void cancelConfirmedEnrollmentReleasesSeat() {
        Classroom classroom = buildClassroom(1);
        Enrollment enrollment = new Enrollment(buildStudent(), classroom);
        enrollment.confirm();

        enrollment.cancel();

        assertEquals(EnrollmentStatus.CANCELLED, enrollment.getStatus());
        classroom.consumeSeat();
    }

    @Test
    void cancelPendingEnrollmentOnlyChangesStatus() {
        Enrollment enrollment = new Enrollment(buildStudent(), buildClassroom(1));

        enrollment.cancel();

        assertEquals(EnrollmentStatus.CANCELLED, enrollment.getStatus());
    }

    private static Student buildStudent() {
        return new Student(UUID.randomUUID(), "Alice");
    }

    private static Classroom buildClassroom(int seats) {
        return new Classroom(
                UUID.randomUUID(),
                new Discipline(UUID.randomUUID(), "Math"),
                new SeatLimit(seats),
                new EnrollmentPeriod(LocalDate.now().minusDays(1), LocalDate.now().plusDays(1))
        );
    }

    private static Classroom buildClassroomWithNoAvailableSeats() {
        Classroom classroom = buildClassroom(1);
        classroom.consumeSeat();
        return classroom;
    }
}
