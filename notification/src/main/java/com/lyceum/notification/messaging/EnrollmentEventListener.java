package com.lyceum.notification.messaging;

import com.lyceum.notification.entity.Notification;
import com.lyceum.notification.service.NotificationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Listener for enrollment events from RabbitMQ.
 * This listener receives messages from the "enrollment.events" queue and processes them to create notifications.
 */
@Component
public class EnrollmentEventListener {

    private final NotificationService notificationService;

    public EnrollmentEventListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @RabbitListener(queues = "enrollment.events")
    public void handleEnrollmentEvent(String message) {
        // Aqui você pode mapear o JSON recebido para um objeto Notification
        Notification notification = new Notification("Enrollment event received: " + message);
        notificationService.sendNotification(notification);
    }
}
