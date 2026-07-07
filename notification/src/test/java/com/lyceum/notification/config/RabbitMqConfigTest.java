package com.lyceum.notification.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RabbitMqConfigTest {

    private final RabbitMqConfig config = new RabbitMqConfig();

    @Mock
    private RabbitTemplate rabbitTemplate;

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

    // --- Retry interceptor ---

    @Test
    void retryInterceptorIsNotNull() {
        RetryOperationsInterceptor interceptor = config.retryInterceptor(rabbitTemplate);
        assertNotNull(interceptor);
    }

    @Test
    void retryConstantsAreValid() {
        assertTrue(RabbitMqConfig.MAX_ATTEMPTS > 1,
                "MAX_ATTEMPTS deve ser maior que 1 para que haja pelo menos uma nova tentativa");
        assertTrue(RabbitMqConfig.INITIAL_INTERVAL_MS > 0,
                "INITIAL_INTERVAL_MS deve ser positivo");
        assertTrue(RabbitMqConfig.BACKOFF_MULTIPLIER > 1.0,
                "BACKOFF_MULTIPLIER deve ser maior que 1.0 para backoff crescente");
        assertTrue(RabbitMqConfig.MAX_INTERVAL_MS >= RabbitMqConfig.INITIAL_INTERVAL_MS,
                "MAX_INTERVAL_MS deve ser >= INITIAL_INTERVAL_MS");
    }
}
