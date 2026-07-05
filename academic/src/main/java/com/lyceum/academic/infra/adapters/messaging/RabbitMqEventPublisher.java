package com.lyceum.academic.infra.adapters.messaging;

import com.lyceum.academic.application.ports.EventPublisher;
import com.lyceum.academic.domain.event.EnrollmentCancelled;
import com.lyceum.academic.domain.event.EnrollmentConfirmed;
import com.lyceum.academic.domain.event.EnrollmentCreated;
import com.lyceum.academic.domain.event.EnrollmentEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RabbitMqEventPublisher implements EventPublisher {

    private static final String EXCHANGE = "enrollment.events.exchange";
    private static final String ROUTING_KEY = "enrollment.events";

    private final RabbitTemplate rabbitTemplate;

    public RabbitMqEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publish(EnrollmentCreated event) {
        send("MatriculaCriada", event.getEnrollmentId(), event.getStudentId(), event.getClassroomId(), event.getOccurredAt());
    }

    @Override
    public void publish(EnrollmentConfirmed event) {
        send("MatriculaConfirmada", event.getEnrollmentId(), event.getStudentId(), event.getClassroomId(), event.getOccurredAt());
    }

    @Override
    public void publish(EnrollmentCancelled event) {
        send("MatriculaCancelada", event.getEnrollmentId(), event.getStudentId(), event.getClassroomId(), event.getOccurredAt());
    }

    private void send(String type, UUID enrollmentId, UUID studentId, UUID classroomId, java.time.Instant occurredAt) {
        EnrollmentEvent payload = new EnrollmentEvent(UUID.randomUUID(), type, enrollmentId, studentId, classroomId, occurredAt);
        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, payload);
    }
}
