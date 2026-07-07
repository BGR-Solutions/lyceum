package com.lyceum.notification.messaging;

import com.lyceum.notification.repository.NotificationRepository;
import com.lyceum.notification.repository.ProcessedEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for EnrollmentEventListener.
 *
 * Uses H2 in-memory database and disables RabbitMQ listener auto-start so no
 * real broker connection is needed. The listener method is invoked directly,
 * exercising the full path: idempotency check → notification service → repository.
 *
 * Covers:
 *  - Notification persisted when a new event is received
 *  - ProcessedEvent record created after processing
 *  - Duplicate event (same eventId) is silently ignored
 *  - Event with null eventId is ignored without error
 *  - All three event types trigger notification creation
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "spring.datasource.url=jdbc:h2:mem:notificationtest;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH",
                "spring.datasource.driver-class-name=org.h2.Driver",
                "spring.datasource.username=sa",
                "spring.datasource.password=",
                "spring.jpa.hibernate.ddl-auto=create-drop",
                "spring.flyway.enabled=false",
                "spring.rabbitmq.listener.simple.auto-startup=false",
                "management.health.rabbit.enabled=false"
        }
)
class EnrollmentEventListenerIntegrationTest {

    @Autowired
    private EnrollmentEventListener listener;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private ProcessedEventRepository processedEventRepository;

    @BeforeEach
    void cleanUp() {
        notificationRepository.deleteAll();
        processedEventRepository.deleteAll();
    }

    // ── helpers ────────────────────────────────────────────────────────────────

    private EnrollmentEventMessage message(String eventType) {
        return new EnrollmentEventMessage(
                UUID.randomUUID(),
                eventType,
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                Instant.now()
        );
    }

    // ── tests ──────────────────────────────────────────────────────────────────

    @Test
    void handleEvent_persistsNotification() {
        listener.handleEnrollmentEvent(message("MatriculaCriada"));

        assertEquals(1, notificationRepository.count());
    }

    @Test
    void handleEvent_marksEventIdAsProcessed() {
        EnrollmentEventMessage msg = message("MatriculaCriada");

        listener.handleEnrollmentEvent(msg);

        assertTrue(processedEventRepository.existsById(msg.eventId()));
    }

    @Test
    void handleEvent_duplicateEventId_processedOnlyOnce() {
        EnrollmentEventMessage msg = message("MatriculaConfirmada");

        listener.handleEnrollmentEvent(msg);
        listener.handleEnrollmentEvent(msg); // second call with same eventId

        assertEquals(1, notificationRepository.count());
        assertEquals(1, processedEventRepository.count());
    }

    @Test
    void handleEvent_nullEventId_skipsProcessingWithoutError() {
        EnrollmentEventMessage msg = new EnrollmentEventMessage(
                null, "MatriculaCriada",
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                Instant.now()
        );

        assertDoesNotThrow(() -> listener.handleEnrollmentEvent(msg));

        assertEquals(0, notificationRepository.count());
        assertEquals(0, processedEventRepository.count());
    }

    @Test
    void handleEvent_allThreeEventTypes_eachCreatesNotification() {
        listener.handleEnrollmentEvent(message("MatriculaCriada"));
        listener.handleEnrollmentEvent(message("MatriculaConfirmada"));
        listener.handleEnrollmentEvent(message("MatriculaCancelada"));

        assertEquals(3, notificationRepository.count());
        assertEquals(3, processedEventRepository.count());
    }

    @Test
    void handleEvent_notificationMessageContainsEventTypeAndEnrollmentId() {
        EnrollmentEventMessage msg = message("MatriculaConfirmada");

        listener.handleEnrollmentEvent(msg);

        String savedMessage = notificationRepository.findAll().get(0).getMessage();
        assertTrue(savedMessage.contains("MatriculaConfirmada"));
        assertTrue(savedMessage.contains(msg.enrollmentId().toString()));
    }
}
