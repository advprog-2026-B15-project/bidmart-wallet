package com.example.wallet.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Exchange that auction publishes to (wallet consumes from)
    public static final String WALLET_EXCHANGE = "wallet.exchange";

    // Exchange that wallet publishes to (auction consumes from)
    public static final String WALLET_EVENTS_EXCHANGE = "wallet.events.exchange";

    // Inbound queues (auction -> wallet)
    public static final String HOLD_QUEUE = "wallet.hold.queue";
    public static final String RELEASE_QUEUE = "wallet.release.queue";
    public static final String CONVERT_QUEUE = "wallet.convert.queue";

    // Routing keys for inbound
    public static final String HOLD_ROUTING_KEY = "wallet.hold";
    public static final String RELEASE_ROUTING_KEY = "wallet.release";
    public static final String CONVERT_ROUTING_KEY = "wallet.convert";

    // Routing keys for outbound events
    public static final String BALANCE_RELEASED_ROUTING_KEY = "wallet.event.balance-released";
    public static final String PAYMENT_COMPLETED_ROUTING_KEY = "wallet.event.payment-completed";

    // --- Exchanges ---

    @Bean
    public TopicExchange walletExchange() {
        return new TopicExchange(WALLET_EXCHANGE);
    }

    @Bean
    public TopicExchange walletEventsExchange() {
        return new TopicExchange(WALLET_EVENTS_EXCHANGE);
    }

    // --- Queues ---

    @Bean
    public Queue holdQueue() {
        return QueueBuilder.durable(HOLD_QUEUE).build();
    }

    @Bean
    public Queue releaseQueue() {
        return QueueBuilder.durable(RELEASE_QUEUE).build();
    }

    @Bean
    public Queue convertQueue() {
        return QueueBuilder.durable(CONVERT_QUEUE).build();
    }

    // --- Bindings ---

    @Bean
    public Binding holdBinding(Queue holdQueue, TopicExchange walletExchange) {
        return BindingBuilder.bind(holdQueue).to(walletExchange).with(HOLD_ROUTING_KEY);
    }

    @Bean
    public Binding releaseBinding(Queue releaseQueue, TopicExchange walletExchange) {
        return BindingBuilder.bind(releaseQueue).to(walletExchange).with(RELEASE_ROUTING_KEY);
    }

    @Bean
    public Binding convertBinding(Queue convertQueue, TopicExchange walletExchange) {
        return BindingBuilder.bind(convertQueue).to(walletExchange).with(CONVERT_ROUTING_KEY);
    }

    // --- Message Converter ---

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        return template;
    }
}
