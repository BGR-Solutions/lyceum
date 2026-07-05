package com.lyceum.notification.service;

import com.lyceum.notification.entity.Notification;
import com.lyceum.notification.repository.NotificationRepository;
import com.lyceum.notification.sender.NotificationSender;
import org.springframework.stereotype.Service;

/**
 * Service class for managing notifications.
 * This service provides methods to send notifications and interact with the notification repository.
 */
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationSender notificationSender;

    public NotificationService(NotificationRepository notificationRepository,
                               NotificationSender notificationSender) {
        this.notificationRepository = notificationRepository;
        this.notificationSender = notificationSender;
    }

    public void sendNotification(Notification notification) {
        notificationRepository.save(notification);
        notificationSender.send(notification);
    }
}
