package com.lyceum.notification.service;

import com.lyceum.notification.entity.Notification;
import com.lyceum.notification.entity.NotificationType;
import com.lyceum.notification.repository.NotificationRepository;
import com.lyceum.notification.sender.NotificationSender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationSender senderA;

    @Mock
    private NotificationSender senderB;

    // --- Regra: notificação deve ser persistida e enviada por todos os senders ---

    @Test
    void sendNotificationPersistsBeforeDispatching() {
        NotificationService service = new NotificationService(notificationRepository, List.of(senderA));
        Notification notification = new Notification("persist test");

        service.sendNotification(notification);

        verify(notificationRepository).save(notification);
    }

    @Test
    void sendNotificationDelegatesToAllSenders() {
        NotificationService service = new NotificationService(notificationRepository, List.of(senderA, senderB));
        Notification notification = new Notification("dispatch test");

        service.sendNotification(notification);

        verify(senderA).send(notification);
        verify(senderB).send(notification);
    }

    @Test
    void sendNotificationWithSingleSenderCallsItOnce() {
        NotificationService service = new NotificationService(notificationRepository, List.of(senderA));
        Notification notification = new Notification("single sender");

        service.sendNotification(notification);

        verify(senderA, times(1)).send(notification);
    }

    @Test
    void sendNotificationWithNoSendersOnlyPersists() {
        NotificationService service = new NotificationService(notificationRepository, List.of());
        Notification notification = new Notification("no senders");

        service.sendNotification(notification);

        verify(notificationRepository).save(notification);
        verify(senderA, never()).send(any());
        verify(senderB, never()).send(any());
    }

    @Test
    void sendNotificationWithEmailTypeDispatchesToAllSenders() {
        NotificationService service = new NotificationService(notificationRepository, List.of(senderA, senderB));
        Notification notification = new Notification("email body", NotificationType.EMAIL);

        service.sendNotification(notification);

        verify(senderA).send(notification);
        verify(senderB).send(notification);
    }
}
