package com.lyceum.academic.domain.entity;

import com.lyceum.academic.domain.enums.ClassroomStatus;
import com.lyceum.academic.domain.valueobject.EnrollmentPeriod;
import com.lyceum.academic.domain.valueobject.SeatLimit;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "classrooms")
public class Classroom {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discipline_id", nullable = false)
    private Discipline discipline;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClassroomStatus status;

    @Embedded
    private SeatLimit seatLimit;

    @Embedded
    private EnrollmentPeriod enrollmentPeriod;

    @Version
    private Long version;

    protected Classroom() {
    }

    public Classroom(UUID id, Discipline discipline, SeatLimit seatLimit, EnrollmentPeriod enrollmentPeriod) {
        this.id = id == null ? UUID.randomUUID() : id;
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
    public SeatLimit getSeatLimit() { return seatLimit; }
    public EnrollmentPeriod getEnrollmentPeriod() { return enrollmentPeriod; }
    public Long getVersion() { return version; }
    public void setStatus(ClassroomStatus status) { this.status = status; }
}
