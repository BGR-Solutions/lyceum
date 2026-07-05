package com.lyceum.academic.infra.adapters.messaging;

import com.lyceum.academic.domain.event.EnrollmentCancelled;
import com.lyceum.academic.domain.event.EnrollmentConfirmed;
import com.lyceum.academic.domain.event.EnrollmentCreated;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RabbitMqEventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Test
    void publishEnrollmentCreatedSendsToExpectedQueue() {
        RabbitMqEventPublisher publisher = new RabbitMqEventPublisher(rabbitTemplate);
        EnrollmentCreated event = new EnrollmentCreated(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

        publisher.publish(event);

        verify(rabbitTemplate).convertAndSend(eq("enrollment.events.exchange"), eq("enrollment.events"), any(Object.class));
    }

    @Test
    void publishEnrollmentConfirmedSendsToExpectedQueue() {
        RabbitMqEventPublisher publisher = new RabbitMqEventPublisher(rabbitTemplate);
        EnrollmentConfirmed event = new EnrollmentConfirmed(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

        publisher.publish(event);

        verify(rabbitTemplate).convertAndSend(eq("enrollment.events.exchange"), eq("enrollment.events"), any(Object.class));
    }

    @Test
    void publishEnrollmentCancelledSendsToExpectedQueue() {
        RabbitMqEventPublisher publisher = new RabbitMqEventPublisher(rabbitTemplate);
        EnrollmentCancelled event = new EnrollmentCancelled(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

        publisher.publish(event);

        verify(rabbitTemplate).convertAndSend(eq("enrollment.events.exchange"), eq("enrollment.events"), any(Object.class));
    }
}
