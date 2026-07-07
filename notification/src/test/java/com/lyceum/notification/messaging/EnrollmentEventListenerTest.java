package com.lyceum.notification.messaging;

import com.lyceum.notification.entity.Notification;
import com.lyceum.notification.entity.ProcessedEvent;
import com.lyceum.notification.repository.ProcessedEventRepository;
import com.lyceum.notification.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentEventListenerTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private ProcessedEventRepository processedEventRepository;

    @InjectMocks
    private EnrollmentEventListener listener;

    // --- Regra: evento novo deve gerar notificação e ser registrado ---

    @Test
    void processesNewEventSendsNotification() {
        UUID eventId = UUID.randomUUID();
        EnrollmentEventMessage message = buildMessage(eventId, "ENROLLMENT_CREATED");
        when(processedEventRepository.existsById(eventId)).thenReturn(false);

        listener.handleEnrollmentEvent(message);

        verify(notificationService).sendNotification(any(Notification.class));
    }

    @Test
    void processesNewEventPersistsProcessedEvent() {
        UUID eventId = UUID.randomUUID();
        EnrollmentEventMessage message = buildMessage(eventId, "ENROLLMENT_CREATED");
        when(processedEventRepository.existsById(eventId)).thenReturn(false);

        listener.handleEnrollmentEvent(message);

        ArgumentCaptor<ProcessedEvent> captor = ArgumentCaptor.forClass(ProcessedEvent.class);
        verify(processedEventRepository).save(captor.capture());
        assertEquals(eventId, captor.getValue().getEventId());
    }

    // --- Regra: idempotência — evento duplicado não deve gerar nova notificação ---

    @Test
    void ignoresDuplicateEventDoesNotSendNotification() {
        UUID eventId = UUID.randomUUID();
        EnrollmentEventMessage message = buildMessage(eventId, "ENROLLMENT_CREATED");
        when(processedEventRepository.existsById(eventId)).thenReturn(true);

        listener.handleEnrollmentEvent(message);

        verify(notificationService, never()).sendNotification(any());
    }

    @Test
    void ignoresDuplicateEventDoesNotPersist() {
        UUID eventId = UUID.randomUUID();
        EnrollmentEventMessage message = buildMessage(eventId, "ENROLLMENT_CREATED");
        when(processedEventRepository.existsById(eventId)).thenReturn(true);

        listener.handleEnrollmentEvent(message);

        verify(processedEventRepository, never()).save(any());
    }

    // --- Regra: evento sem eventId deve ser ignorado ---

    @Test
    void ignoresEventWithNullEventId() {
        EnrollmentEventMessage message = new EnrollmentEventMessage(
                null, "ENROLLMENT_CREATED", UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), Instant.now());

        listener.handleEnrollmentEvent(message);

        verify(notificationService, never()).sendNotification(any());
        verify(processedEventRepository, never()).save(any());
        verify(processedEventRepository, never()).existsById(any());
    }

    // --- Regra: mensagem de notificação deve conter tipo de evento e id da matrícula ---

    @Test
    void notificationMessageContainsEventType() {
        UUID eventId = UUID.randomUUID();
        EnrollmentEventMessage message = buildMessage(eventId, "ENROLLMENT_CONFIRMED");
        when(processedEventRepository.existsById(eventId)).thenReturn(false);

        listener.handleEnrollmentEvent(message);

        ArgumentCaptor<Notification> notifCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationService).sendNotification(notifCaptor.capture());
        assertTrue(notifCaptor.getValue().getMessage().contains("ENROLLMENT_CONFIRMED"));
    }

    @Test
    void notificationMessageContainsEnrollmentId() {
        UUID eventId = UUID.randomUUID();
        UUID enrollmentId = UUID.randomUUID();
        EnrollmentEventMessage message = new EnrollmentEventMessage(
                eventId, "ENROLLMENT_CANCELLED", enrollmentId, UUID.randomUUID(), UUID.randomUUID(), Instant.now());
        when(processedEventRepository.existsById(eventId)).thenReturn(false);

        listener.handleEnrollmentEvent(message);

        ArgumentCaptor<Notification> notifCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationService).sendNotification(notifCaptor.capture());
        assertTrue(notifCaptor.getValue().getMessage().contains(enrollmentId.toString()));
    }

    // --- Regra: diferentes tipos de eventos devem ser processados corretamente ---

    @Test
    void processesEnrollmentConfirmedEvent() {
        UUID eventId = UUID.randomUUID();
        EnrollmentEventMessage message = buildMessage(eventId, "ENROLLMENT_CONFIRMED");
        when(processedEventRepository.existsById(eventId)).thenReturn(false);

        listener.handleEnrollmentEvent(message);

        verify(notificationService).sendNotification(any(Notification.class));
        verify(processedEventRepository).save(any(ProcessedEvent.class));
    }

    @Test
    void processesEnrollmentCancelledEvent() {
        UUID eventId = UUID.randomUUID();
        EnrollmentEventMessage message = buildMessage(eventId, "ENROLLMENT_CANCELLED");
        when(processedEventRepository.existsById(eventId)).thenReturn(false);

        listener.handleEnrollmentEvent(message);

        verify(notificationService).sendNotification(any(Notification.class));
        verify(processedEventRepository).save(any(ProcessedEvent.class));
    }

    // --- Helper ---

    private EnrollmentEventMessage buildMessage(UUID eventId, String eventType) {
        return new EnrollmentEventMessage(
                eventId, eventType, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), Instant.now());
    }
}
