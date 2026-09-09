package com.juanfedevmaster.billingmicroservices.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.queue.billing}")
    private String billingQueue;

    @Value("${rabbitmq.routing-key}")
    private String routingKey;

    @Bean
    TopicExchange salesExchange() {
        return new TopicExchange(exchange, true, false);
    }

    @Bean
    Queue billingQueue() {
        return QueueBuilder.durable(billingQueue).build();
    }

    @Bean
    Binding billingBinding(Queue billingQueue, TopicExchange salesExchange) {
        return BindingBuilder.bind(billingQueue).to(salesExchange).with(routingKey);
    }

    @Bean
    MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
