package com.lyceum.academic.infra.adapters.messaging;

import com.lyceum.academic.domain.event.EnrollmentCancelled;
import com.lyceum.academic.domain.event.EnrollmentConfirmed;
import com.lyceum.academic.domain.event.EnrollmentCreated;
import com.lyceum.academic.domain.event.EnrollmentEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Verifica que cada evento de matrícula é publicado com o payload correto no RabbitMQ.
 *
 * Estratégia: usar ArgumentCaptor para inspecionar o EnrollmentEvent enviado ao broker,
 * garantindo que tipo, IDs e timestamp estejam presentes e coerentes com o evento de domínio.
 */
@ExtendWith(MockitoExtension.class)
class EnrollmentEventPublicationTest {

    private static final String EXCHANGE    = "enrollment.events.exchange";
    private static final String ROUTING_KEY = "enrollment.events";

    @Mock
    private RabbitTemplate rabbitTemplate;

    // ------------------------------------------------------------------ //
    //  EnrollmentCreated                                                   //
    // ------------------------------------------------------------------ //

    @Test
    void publishEnrollmentCreated_payloadCarriesCorrectTypeAndIds() {
        UUID enrollmentId = UUID.randomUUID();
        UUID studentId    = UUID.randomUUID();
        UUID classroomId  = UUID.randomUUID();

        RabbitMqEventPublisher publisher = new RabbitMqEventPublisher(rabbitTemplate);
        publisher.publish(new EnrollmentCreated(enrollmentId, studentId, classroomId));

        EnrollmentEvent payload = capturePayload();

        assertNotNull(payload.eventId(),   "eventId deve ser gerado automaticamente");
        assertEquals("MatriculaCriada",    payload.eventType());
        assertEquals(enrollmentId,          payload.enrollmentId());
        assertEquals(studentId,             payload.studentId());
        assertEquals(classroomId,           payload.classroomId());
        assertNotNull(payload.occurredAt(), "occurredAt não pode ser nulo");
    }

    @Test
    void publishEnrollmentCreated_eachPublicationGeneratesUniqueEventId() {
        UUID enrollmentId = UUID.randomUUID();
        RabbitMqEventPublisher publisher = new RabbitMqEventPublisher(rabbitTemplate);

        publisher.publish(new EnrollmentCreated(enrollmentId, UUID.randomUUID(), UUID.randomUUID()));
        publisher.publish(new EnrollmentCreated(enrollmentId, UUID.randomUUID(), UUID.randomUUID()));

        ArgumentCaptor<EnrollmentEvent> captor = ArgumentCaptor.forClass(EnrollmentEvent.class);
        verify(rabbitTemplate, times(2))
                .convertAndSend(eq(EXCHANGE), eq(ROUTING_KEY), captor.capture());

        List<EnrollmentEvent> all = captor.getAllValues();
        assertNotEquals(all.get(0).eventId(), all.get(1).eventId(),
                "Cada publicação deve gerar um eventId único (idempotência de envio)");
    }

    // ------------------------------------------------------------------ //
    //  EnrollmentConfirmed                                                 //
    // ------------------------------------------------------------------ //

    @Test
    void publishEnrollmentConfirmed_payloadCarriesCorrectTypeAndIds() {
        UUID enrollmentId = UUID.randomUUID();
        UUID studentId    = UUID.randomUUID();
        UUID classroomId  = UUID.randomUUID();

        RabbitMqEventPublisher publisher = new RabbitMqEventPublisher(rabbitTemplate);
        publisher.publish(new EnrollmentConfirmed(enrollmentId, studentId, classroomId));

        EnrollmentEvent payload = capturePayload();

        assertEquals("MatriculaConfirmada", payload.eventType());
        assertEquals(enrollmentId,           payload.enrollmentId());
        assertEquals(studentId,              payload.studentId());
        assertEquals(classroomId,            payload.classroomId());
    }

    // ------------------------------------------------------------------ //
    //  EnrollmentCancelled                                                 //
    // ------------------------------------------------------------------ //

    @Test
    void publishEnrollmentCancelled_payloadCarriesCorrectTypeAndIds() {
        UUID enrollmentId = UUID.randomUUID();
        UUID studentId    = UUID.randomUUID();
        UUID classroomId  = UUID.randomUUID();

        RabbitMqEventPublisher publisher = new RabbitMqEventPublisher(rabbitTemplate);
        publisher.publish(new EnrollmentCancelled(enrollmentId, studentId, classroomId));

        EnrollmentEvent payload = capturePayload();

        assertEquals("MatriculaCancelada", payload.eventType());
        assertEquals(enrollmentId,          payload.enrollmentId());
        assertEquals(studentId,             payload.studentId());
        assertEquals(classroomId,           payload.classroomId());
    }

    // ------------------------------------------------------------------ //
    //  Helpers                                                             //
    // ------------------------------------------------------------------ //

    private EnrollmentEvent capturePayload() {
        ArgumentCaptor<EnrollmentEvent> captor = ArgumentCaptor.forClass(EnrollmentEvent.class);
        verify(rabbitTemplate).convertAndSend(eq(EXCHANGE), eq(ROUTING_KEY), captor.capture());
        return captor.getValue();
    }
}
