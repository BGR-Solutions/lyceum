package com.lyceum.academic.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for messaging-related beans and settings.
 * This class defines the necessary beans for message queues used in the application.
 */
@Configuration
public class MessagingConfig {

    public static final String ENROLLMENT_QUEUE = "enrollment.events";

    @Bean
    public Queue enrollmentQueue() {
        return new Queue(ENROLLMENT_QUEUE, true);
    }
}
