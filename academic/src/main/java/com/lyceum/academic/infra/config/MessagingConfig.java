package com.lyceum.academic.infra.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessagingConfig {

    public static final String EXCHANGE = "enrollment.events.exchange";
    public static final String ENROLLMENT_QUEUE = "enrollment.events";
    public static final String ENROLLMENT_DLQ = "enrollment.events.dlq";

    @Bean
    public DirectExchange enrollmentExchange() {
        return new DirectExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue enrollmentQueue() {
        return QueueBuilder.durable(ENROLLMENT_QUEUE)
                .withArgument("x-dead-letter-exchange", EXCHANGE)
                .withArgument("x-dead-letter-routing-key", ENROLLMENT_DLQ)
                .build();
    }

    @Bean
    public Queue enrollmentDlq() {
        return QueueBuilder.durable(ENROLLMENT_DLQ).build();
    }

    @Bean
    public Binding enrollmentBinding() {
        return BindingBuilder.bind(enrollmentQueue()).to(enrollmentExchange()).with(ENROLLMENT_QUEUE);
    }

    @Bean
    public Binding enrollmentDlqBinding() {
        return BindingBuilder.bind(enrollmentDlq()).to(enrollmentExchange()).with(ENROLLMENT_DLQ);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
