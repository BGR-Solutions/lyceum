package com.lyceum.notification.sender;

import com.lyceum.notification.entity.Notification;

/**
 * Interface for sending notifications.
 * This interface defines the contract for sending notifications, allowing different implementations to be used.
 */
public interface NotificationSender {
    void send(Notification notification);
}
