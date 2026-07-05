package com.lyceum.notification.sender;

import com.lyceum.notification.entity.Notification;
import org.springframework.stereotype.Component;

/**
 * Implementation of NotificationSender that sends notifications via email.
 * This class provides the logic for sending email notifications.
 */
@Component
public class EmailNotificationSender implements NotificationSender {

    @Override
    public void send(Notification notification) {
        // Lógica para envio de e-mail
        System.out.println("Sending EMAIL notification: " + notification.getMessage());
    }
}
