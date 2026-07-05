package com.lyceum.academic.application.command;

import java.util.UUID;

/**
 * Command to request the cancellation of an existing enrollment.
 */
public class CancelEnrollmentCommand {
    private final UUID enrollmentId;

    public CancelEnrollmentCommand(UUID enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public UUID getEnrollmentId() { return enrollmentId; }
}
