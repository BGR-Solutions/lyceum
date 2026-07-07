package com.lyceum.notification.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotificationTypeTest {

    @Test
    void allExpectedTypesExist() {
        assertNotNull(NotificationType.INFO);
        assertNotNull(NotificationType.EMAIL);
        assertNotNull(NotificationType.PUSH);
        assertNotNull(NotificationType.SMS);
    }

    @Test
    void enumContainsFourTypes() {
        assertEquals(4, NotificationType.values().length);
    }

    @Test
    void valueOfReturnsCorrectConstants() {
        assertEquals(NotificationType.INFO, NotificationType.valueOf("INFO"));
        assertEquals(NotificationType.EMAIL, NotificationType.valueOf("EMAIL"));
        assertEquals(NotificationType.PUSH, NotificationType.valueOf("PUSH"));
        assertEquals(NotificationType.SMS, NotificationType.valueOf("SMS"));
    }
}
