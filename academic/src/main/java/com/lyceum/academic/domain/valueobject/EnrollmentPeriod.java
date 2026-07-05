package com.lyceum.academic.domain.valueobject;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.time.LocalDate;

/**
 * Value object representing the enrollment period for a classroom.
 * It defines the start and end dates of the enrollment period and provides a method to check if the enrollment is currently open.
 */
@Embeddable
public class EnrollmentPeriod {
    @Column(name = "enrollment_start", nullable = false)
    private LocalDate startDate;

    @Column(name = "enrollment_end", nullable = false)
    private LocalDate endDate;

    protected EnrollmentPeriod() {
    }

    public EnrollmentPeriod(LocalDate startDate, LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public boolean isOpen(LocalDate today) {
        return !today.isBefore(startDate) && !today.isAfter(endDate);
    }

    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
}
