package com.lyceum.notification.sender;

import com.lyceum.notification.entity.Notification;
import com.lyceum.notification.entity.NotificationType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class PushNotificationSenderTest {

    private final PushNotificationSender sender = new PushNotificationSender();

    @Test
    void sendInfoNotificationDoesNotThrow() {
        Notification notification = new Notification("push: enrollment pending");
        assertDoesNotThrow(() -> sender.send(notification));
    }

    @Test
    void sendPushTypeNotificationDoesNotThrow() {
        Notification notification = new Notification("push content", NotificationType.PUSH);
        assertDoesNotThrow(() -> sender.send(notification));
    }
}
