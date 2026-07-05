package com.lyceum.notification.sender;

import com.lyceum.notification.entity.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * {@link NotificationSender} implementation that delivers notifications via email.
 */
@Component
public class EmailNotificationSender implements NotificationSender {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationSender.class);

    @Override
    public void send(Notification notification) {
        log.info("Sending EMAIL notification: {}", notification.getMessage());
    }
}
