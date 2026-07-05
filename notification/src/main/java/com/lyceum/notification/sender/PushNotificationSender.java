package com.lyceum.notification.sender;

import com.lyceum.notification.entity.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * {@link NotificationSender} implementation that delivers push notifications.
 */
@Component
public class PushNotificationSender implements NotificationSender {

    private static final Logger log = LoggerFactory.getLogger(PushNotificationSender.class);

    @Override
    public void send(Notification notification) {
        log.info("Sending PUSH notification: {}", notification.getMessage());
    }
}
