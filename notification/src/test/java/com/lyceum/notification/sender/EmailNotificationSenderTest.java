package com.lyceum.notification.sender;

import com.lyceum.notification.entity.Notification;
import com.lyceum.notification.entity.NotificationType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class EmailNotificationSenderTest {

    private final EmailNotificationSender sender = new EmailNotificationSender();

    @Test
    void sendInfoNotificationDoesNotThrow() {
        Notification notification = new Notification("email subject: enrollment confirmed");
        assertDoesNotThrow(() -> sender.send(notification));
    }

    @Test
    void sendEmailTypeNotificationDoesNotThrow() {
        Notification notification = new Notification("email body", NotificationType.EMAIL);
        assertDoesNotThrow(() -> sender.send(notification));
    }
}
