package com.lyceum.academic.application.ports;

import com.lyceum.academic.domain.event.EnrollmentCreated;
import com.lyceum.academic.domain.event.EnrollmentConfirmed;
import com.lyceum.academic.domain.event.EnrollmentCancelled;

/**
 * Port to publish domain events to external systems (e.g., RabbitMQ).
 */
public interface EventPublisher {
    void publish(EnrollmentCreated event);
    void publish(EnrollmentConfirmed event);
    void publish(EnrollmentCancelled event);
}
