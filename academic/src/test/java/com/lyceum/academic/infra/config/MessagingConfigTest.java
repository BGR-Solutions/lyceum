package com.lyceum.academic.infra.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MessagingConfigTest {

    @Test
    void enrollmentQueueUsesExpectedNameAndDurability() {
        MessagingConfig config = new MessagingConfig();

        org.springframework.amqp.core.Queue queue = config.enrollmentQueue();

        assertEquals(MessagingConfig.ENROLLMENT_QUEUE, queue.getName());
        assertTrue(queue.isDurable());
    }
}
