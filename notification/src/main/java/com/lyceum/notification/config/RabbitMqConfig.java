package com.lyceum.notification.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Bean
    public DirectExchange enrollmentExchange() {
        return new DirectExchange("enrollment.events.exchange", true, false);
    }

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

    @Bean
    public Binding enrollmentBinding() {
        return BindingBuilder.bind(enrollmentEventsQueue()).to(enrollmentExchange()).with("enrollment.events");
    }

    @Bean
    public Binding enrollmentDlqBinding() {
        return BindingBuilder.bind(enrollmentEventsDlq()).to(enrollmentExchange()).with("enrollment.events.dlq");
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory,
                                                                               Jackson2JsonMessageConverter converter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(converter);
        factory.setDefaultRequeueRejected(false);
        return factory;
    }
}
