package com.lyceum.notification.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotificationTest {

    @Test
    void constructorWithMessageDefaultsToInfo() {
        Notification notification = new Notification("test message");

        assertEquals("test message", notification.getMessage());
        assertEquals(NotificationType.INFO, notification.getType());
        assertNotNull(notification.getCreatedAt());
    }

    @Test
    void constructorWithMessageAndTypeSetsCorrectType() {
        Notification notification = new Notification("email content", NotificationType.EMAIL);

        assertEquals("email content", notification.getMessage());
        assertEquals(NotificationType.EMAIL, notification.getType());
    }

    @Test
    void constructorWithPushTypeSetsCorrectType() {
        Notification notification = new Notification("push content", NotificationType.PUSH);

        assertEquals(NotificationType.PUSH, notification.getType());
    }

    @Test
    void idIsNullBeforePersistence() {
        Notification notification = new Notification("test");

        assertNull(notification.getId());
    }

    @Test
    void createdAtIsNotNull() {
        Notification notification = new Notification("test");

        assertNotNull(notification.getCreatedAt());
    }
}
