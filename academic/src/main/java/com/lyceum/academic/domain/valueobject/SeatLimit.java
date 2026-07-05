package com.lyceum.academic.domain.valueobject;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Value object representing the seat limit of a classroom.
 * It keeps track of the maximum number of seats and the number of occupied seats.
 */
@Embeddable
public class SeatLimit {
    @Column(name = "max_seats", nullable = false)
    private int maxSeats;

    @Column(name = "occupied_seats", nullable = false)
    private int occupiedSeats;

    protected SeatLimit() {
    }

    public SeatLimit(int maxSeats) {
        this(maxSeats, 0);
    }

    public SeatLimit(int maxSeats, int occupiedSeats) {
        if (maxSeats <= 0) throw new IllegalArgumentException("Seats must be positive");
        if (occupiedSeats < 0 || occupiedSeats > maxSeats) throw new IllegalArgumentException("Invalid occupied seats");
        this.maxSeats = maxSeats;
        this.occupiedSeats = occupiedSeats;
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

    public int getMaxSeats() { return maxSeats; }
    public int getOccupiedSeats() { return occupiedSeats; }
}
