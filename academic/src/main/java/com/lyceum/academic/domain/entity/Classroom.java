package com.lyceum.academic.domain.entity;

import com.lyceum.academic.domain.enums.ClassroomStatus;
import com.lyceum.academic.domain.valueobject.SeatLimit;
import com.lyceum.academic.domain.valueobject.EnrollmentPeriod;

import java.util.UUID;
import java.time.LocalDate;

/**
 * Represents a classroom in the academic system.
 * Each classroom has a unique identifier, a discipline, a status, a seat limit, and an enrollment period.
 */
public class Classroom {
    private final UUID id;
    private final Discipline discipline;
    private ClassroomStatus status;
    private final SeatLimit seatLimit;
    private final EnrollmentPeriod enrollmentPeriod;

    public Classroom(UUID id, Discipline discipline, SeatLimit seatLimit, EnrollmentPeriod enrollmentPeriod) {
        this.id = id;
        this.discipline = discipline;
        this.status = ClassroomStatus.OPEN;
        this.seatLimit = seatLimit;
        this.enrollmentPeriod = enrollmentPeriod;
    }

    public boolean isOpenForEnrollment(LocalDate today) {
        return status == ClassroomStatus.OPEN && enrollmentPeriod.isOpen(today);
    }

    public boolean hasAvailableSeats() {
        return seatLimit.hasAvailableSeats();
    }

    public void consumeSeat() {
        seatLimit.consumeSeat();
    }

    public void releaseSeat() {
        seatLimit.releaseSeat();
    }

    public UUID getId() { return id; }
    public Discipline getDiscipline() { return discipline; }
    public ClassroomStatus getStatus() { return status; }
}
