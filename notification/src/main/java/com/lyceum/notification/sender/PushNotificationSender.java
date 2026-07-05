package com.lyceum.notification.sender;

import com.lyceum.notification.entity.Notification;
import org.springframework.stereotype.Component;

/**
 * Implementation of NotificationSender that sends notifications via push notification.
 * This class provides the logic for sending push notifications.
 */
@Component
public class PushNotificationSender implements NotificationSender {

    @Override
    public void send(Notification notification) {
        // Lógica para envio de push notification
        System.out.println("Sending PUSH notification: " + notification.getMessage());
    }
}
