package com.lyceum.academic.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SeatLimitTest {

    @Test
    void constructorRejectsNonPositiveMaxSeats() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new SeatLimit(0));

        org.junit.jupiter.api.Assertions.assertEquals("Seats must be positive", ex.getMessage());
    }

    @Test
    void consumeSeatUpdatesAvailability() {
        SeatLimit seatLimit = new SeatLimit(1);

        assertTrue(seatLimit.hasAvailableSeats());
        seatLimit.consumeSeat();
        assertFalse(seatLimit.hasAvailableSeats());
    }

    @Test
    void consumeSeatThrowsWhenNoSeatsAvailable() {
        SeatLimit seatLimit = new SeatLimit(1);
        seatLimit.consumeSeat();

        IllegalStateException ex = assertThrows(IllegalStateException.class, seatLimit::consumeSeat);

        org.junit.jupiter.api.Assertions.assertEquals("No seats available", ex.getMessage());
    }

    @Test
    void releaseSeatDoesNotGoNegative() {
        SeatLimit seatLimit = new SeatLimit(1);

        seatLimit.releaseSeat();

        assertTrue(seatLimit.hasAvailableSeats());
    }
}
