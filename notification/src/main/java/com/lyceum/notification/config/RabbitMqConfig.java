package com.lyceum.notification.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for RabbitMQ-related beans.
 * This class defines the necessary queues and other RabbitMQ components for the notification service.
 */
@Configuration
public class RabbitMqConfig {

    @Bean
    public Queue enrollmentEventsQueue() {
        return new Queue("enrollment.events", true);
    }
}
