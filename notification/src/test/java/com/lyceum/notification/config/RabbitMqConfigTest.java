package com.lyceum.notification.config;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

import static org.junit.jupiter.api.Assertions.*;

class RabbitMqConfigTest {

    private final RabbitMqConfig config = new RabbitMqConfig();

    // --- Exchange ---

    @Test
    void enrollmentExchangeHasCorrectName() {
        DirectExchange exchange = config.enrollmentExchange();
        assertEquals("enrollment.events.exchange", exchange.getName());
    }

    @Test
    void enrollmentExchangeIsDurable() {
        DirectExchange exchange = config.enrollmentExchange();
        assertTrue(exchange.isDurable());
    }

    // --- Main queue ---

    @Test
    void enrollmentEventsQueueHasCorrectName() {
        Queue queue = config.enrollmentEventsQueue();
        assertEquals("enrollment.events", queue.getName());
    }

    @Test
    void enrollmentEventsQueueIsDurable() {
        Queue queue = config.enrollmentEventsQueue();
        assertTrue(queue.isDurable());
    }

    @Test
    void enrollmentEventsQueueHasDlqArgument() {
        Queue queue = config.enrollmentEventsQueue();
        assertEquals("enrollment.events.exchange", queue.getArguments().get("x-dead-letter-exchange"));
        assertEquals("enrollment.events.dlq", queue.getArguments().get("x-dead-letter-routing-key"));
    }

    // --- Dead letter queue ---

    @Test
    void enrollmentEventsDlqHasCorrectName() {
        Queue dlq = config.enrollmentEventsDlq();
        assertEquals("enrollment.events.dlq", dlq.getName());
    }

    @Test
    void enrollmentEventsDlqIsDurable() {
        Queue dlq = config.enrollmentEventsDlq();
        assertTrue(dlq.isDurable());
    }

    // --- Bindings ---

    @Test
    void enrollmentBindingIsNotNull() {
        Binding binding = config.enrollmentBinding();
        assertNotNull(binding);
    }

    @Test
    void enrollmentDlqBindingIsNotNull() {
        Binding dlqBinding = config.enrollmentDlqBinding();
        assertNotNull(dlqBinding);
    }

    // --- Message converter ---

    @Test
    void messageConverterIsJacksonConverter() {
        Jackson2JsonMessageConverter converter = config.messageConverter();
        assertNotNull(converter);
    }
}
