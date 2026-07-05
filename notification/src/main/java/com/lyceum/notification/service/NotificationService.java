package com.lyceum.notification.service;

import com.lyceum.notification.entity.Notification;
import com.lyceum.notification.repository.NotificationRepository;
import com.lyceum.notification.sender.NotificationSender;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for managing notifications.
 * This service provides methods to send notifications and interact with the notification repository.
 */
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final List<NotificationSender> notificationSenders;

    public NotificationService(NotificationRepository notificationRepository,
                               List<NotificationSender> notificationSenders) {
        this.notificationRepository = notificationRepository;
        this.notificationSenders = notificationSenders;
    }

    public void sendNotification(Notification notification) {
        notificationRepository.save(notification);
        notificationSenders.forEach(sender -> sender.send(notification));
    }
}
