package com.lyceum.academic.domain.valueobject;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EnrollmentPeriodTest {

    @Test
    void constructorRejectsEndDateBeforeStartDate() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new EnrollmentPeriod(LocalDate.of(2026, 1, 2), LocalDate.of(2026, 1, 1)));

        org.junit.jupiter.api.Assertions.assertEquals("End date cannot be before start date", ex.getMessage());
    }

    @Test
    void isOpenReturnsTrueWithinInclusiveRange() {
        EnrollmentPeriod period = new EnrollmentPeriod(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 10));

        assertTrue(period.isOpen(LocalDate.of(2026, 1, 1)));
        assertTrue(period.isOpen(LocalDate.of(2026, 1, 10)));
        assertTrue(period.isOpen(LocalDate.of(2026, 1, 5)));
    }

    @Test
    void isOpenReturnsFalseOutsideRange() {
        EnrollmentPeriod period = new EnrollmentPeriod(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 10));

        assertFalse(period.isOpen(LocalDate.of(2025, 12, 31)));
        assertFalse(period.isOpen(LocalDate.of(2026, 1, 11)));
    }
}
