package com.lyceum.notification.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.config.StatelessRetryOperationsInterceptorFactoryBean;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

/**
 * Configuration class for RabbitMQ messaging infrastructure in the notification module.
 *
 * <p>Retry strategy: stateless retry with exponential backoff via
 * {@link StatelessRetryOperationsInterceptorFactoryBean}.
 * <ul>
 *   <li>Attempt 1 — immediate</li>
 *   <li>Attempt 2 — after {@value #INITIAL_INTERVAL_MS} ms</li>
 *   <li>Attempt 3 — after {@value #INITIAL_INTERVAL_MS} * {@value #BACKOFF_MULTIPLIER} ms</li>
 *   <li>Exhausted — {@link RepublishMessageRecoverer} republishes explicitly to the DLQ</li>
 * </ul>
 *
 * <p>The main queue's {@code x-dead-letter-routing-key} is kept as a safety net for
 * errors that bypass the interceptor (e.g. deserialization failures before the listener runs).
 */
@Configuration
public class RabbitMqConfig {

    static final int    MAX_ATTEMPTS        = 3;
    static final long   INITIAL_INTERVAL_MS = 2_000L;
    static final double BACKOFF_MULTIPLIER  = 2.0;
    static final long   MAX_INTERVAL_MS     = 30_000L;

    // -------------------------------------------------------------------------
    // Exchange
    // -------------------------------------------------------------------------

    @Bean
    public DirectExchange enrollmentExchange() {
        return new DirectExchange("enrollment.events.exchange", true, false);
    }

    // -------------------------------------------------------------------------
    // Queues
    // -------------------------------------------------------------------------

    @Bean
    public Queue enrollmentEventsQueue() {
        return QueueBuilder.durable("enrollment.events")
                .withArgument("x-dead-letter-exchange", "enrollment.events.exchange")
                .withArgument("x-dead-letter-routing-key", "enrollment.events.dlq")
                .build();
    }

    @Bean
    public Queue enrollmentEventsDlq() {
        return QueueBuilder.durable("enrollment.events.dlq").build();
    }

    // -------------------------------------------------------------------------
    // Bindings
    // -------------------------------------------------------------------------

    @Bean
    public Binding enrollmentBinding() {
        return BindingBuilder.bind(enrollmentEventsQueue()).to(enrollmentExchange()).with("enrollment.events");
    }

    @Bean
    public Binding enrollmentDlqBinding() {
        return BindingBuilder.bind(enrollmentEventsDlq()).to(enrollmentExchange()).with("enrollment.events.dlq");
    }

    // -------------------------------------------------------------------------
    // Serialization
    // -------------------------------------------------------------------------

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // -------------------------------------------------------------------------
    // Retry
    // -------------------------------------------------------------------------

    /**
     * Builds a {@link RetryTemplate} with exponential backoff policy.
     */
    private RetryTemplate buildRetryTemplate() {
        ExponentialBackOffPolicy backOff = new ExponentialBackOffPolicy();
        backOff.setInitialInterval(INITIAL_INTERVAL_MS);
        backOff.setMultiplier(BACKOFF_MULTIPLIER);
        backOff.setMaxInterval(MAX_INTERVAL_MS);

        RetryTemplate template = new RetryTemplate();
        template.setBackOffPolicy(backOff);
        template.setRetryPolicy(new SimpleRetryPolicy(MAX_ATTEMPTS));
        return template;
    }

    /**
     * Stateless retry interceptor with exponential backoff and DLQ republication on exhaustion.
     *
     * <p>{@code @Lazy} on {@link RabbitTemplate} avoids a circular dependency at context startup
     * between the container factory and the template.
     */
    @Bean
    public RetryOperationsInterceptor retryInterceptor(@Lazy RabbitTemplate rabbitTemplate) {
        StatelessRetryOperationsInterceptorFactoryBean factory =
                new StatelessRetryOperationsInterceptorFactoryBean();
        factory.setRetryOperations(buildRetryTemplate());
        factory.setMessageRecoverer(new RepublishMessageRecoverer(
                rabbitTemplate, "enrollment.events.exchange", "enrollment.events.dlq"));
        return factory.getObject();
    }

    // -------------------------------------------------------------------------
    // Listener container factory
    // -------------------------------------------------------------------------

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter converter,
            RetryOperationsInterceptor retryInterceptor) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(converter);
        factory.setDefaultRequeueRejected(false);
        factory.setAdviceChain(retryInterceptor);
        return factory;
    }
}
