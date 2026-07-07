package com.lyceum.notification.controller;

import com.lyceum.notification.entity.Notification;
import com.lyceum.notification.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationController controller;

    // --- Regra: listagem de notificações ---

    @Test
    void getAllNotificationsReturnsAllFromRepository() {
        List<Notification> expected = List.of(
                new Notification("first notification"),
                new Notification("second notification")
        );
        when(notificationRepository.findAll()).thenReturn(expected);

        List<Notification> result = controller.getAllNotifications();

        assertEquals(2, result.size());
        assertEquals(expected, result);
    }

    @Test
    void getAllNotificationsReturnsEmptyListWhenNoneExist() {
        when(notificationRepository.findAll()).thenReturn(List.of());

        List<Notification> result = controller.getAllNotifications();

        assertTrue(result.isEmpty());
    }

    // --- Regra: busca de notificação por ID ---

    @Test
    void getNotificationByIdReturnsMatchingNotification() {
        UUID id = UUID.randomUUID();
        Notification notification = new Notification("found notification");
        when(notificationRepository.findById(id)).thenReturn(Optional.of(notification));

        Notification result = controller.getNotification(id);

        assertEquals("found notification", result.getMessage());
    }

    @Test
    void getNotificationByIdThrowsWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(notificationRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> controller.getNotification(id));
    }

    @Test
    void getNotificationByIdErrorMessageIsDescriptive() {
        UUID id = UUID.randomUUID();
        when(notificationRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> controller.getNotification(id));
        assertEquals("Notification not found", ex.getMessage());
    }
}
