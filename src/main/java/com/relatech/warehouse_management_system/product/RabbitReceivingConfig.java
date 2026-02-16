package com.relatech.warehouse_management_system.product;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitReceivingConfig {

    public static final String PRODUCT_EXCHANGE = "product.exchange";
    public static final String RECEIVING_QUEUE = "q.receiving.product-sync";

    @Bean
    public Queue receivingQueue() {
        return new Queue(RECEIVING_QUEUE, true); // Durable = true per non perdere dati se il container cade
    }

    @Bean
    public TopicExchange productExchange() {
        return new TopicExchange(PRODUCT_EXCHANGE);
    }

    @Bean
    public Binding binding(Queue receivingQueue, TopicExchange productExchange) {
        // Ascoltiamo tutto ciò che riguarda i prodotti (chiave "product.#")
        return BindingBuilder.bind(receivingQueue).to(productExchange).with("product.#");
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}