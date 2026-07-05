package com.lyceum.academic.domain.valueobject;

import java.time.LocalDate;

/**
 * Value object representing the enrollment period for a course or program.
 * It defines the start and end dates of the enrollment period and provides a method to check if the enrollment is currently open.
 */
public class EnrollmentPeriod {
    private final LocalDate startDate;
    private final LocalDate endDate;

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
}
