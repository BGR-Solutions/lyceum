package com.lyceum.academic.domain.valueobject;

/**
 * Value object representing the seat limit of a classroom or course.
 * It keeps track of the maximum number of seats and the number of occupied seats.
 */
public class SeatLimit {
    private final int maxSeats;
    private int occupiedSeats;

    public SeatLimit(int maxSeats) {
        if (maxSeats <= 0) throw new IllegalArgumentException("Seats must be positive");
        this.maxSeats = maxSeats;
        this.occupiedSeats = 0;
    }

    public boolean hasAvailableSeats() {
        return occupiedSeats < maxSeats;
    }

    public void consumeSeat() {
        if (!hasAvailableSeats()) throw new IllegalStateException("No seats available");
        occupiedSeats++;
    }

    public void releaseSeat() {
        if (occupiedSeats > 0) occupiedSeats--;
    }
}
