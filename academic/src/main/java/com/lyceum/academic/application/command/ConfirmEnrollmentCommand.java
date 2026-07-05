package com.lyceum.academic.application.command;

import java.util.UUID;

/**
 * Command to request the confirmation of an existing enrollment.
 */
public class ConfirmEnrollmentCommand {
    private final UUID enrollmentId;

    public ConfirmEnrollmentCommand(UUID enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public UUID getEnrollmentId() { return enrollmentId; }
}
