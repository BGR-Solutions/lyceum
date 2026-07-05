package com.lyceum.academic.infra.adapters.messaging;

import com.lyceum.academic.application.ports.EventPublisher;
import com.lyceum.academic.domain.event.EnrollmentCreated;
import com.lyceum.academic.domain.event.EnrollmentConfirmed;
import com.lyceum.academic.domain.event.EnrollmentCancelled;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * RabbitMqEventPublisher is an implementation of the EventPublisher interface that publishes events to a RabbitMQ message broker.
 * It uses the RabbitTemplate to send messages to specific queues based on the type of event being published.
 */
@Component
public class RabbitMqEventPublisher implements EventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public RabbitMqEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publish(EnrollmentCreated event) {
        rabbitTemplate.convertAndSend("enrollment.events", event);
    }

    @Override
    public void publish(EnrollmentConfirmed event) {
        rabbitTemplate.convertAndSend("enrollment.events", event);
    }

    @Override
    public void publish(EnrollmentCancelled event) {
        rabbitTemplate.convertAndSend("enrollment.events", event);
    }
}
