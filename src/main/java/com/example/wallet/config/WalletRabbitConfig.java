package com.example.wallet.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WalletRabbitConfig {

    public static final String EXCHANGE_WALLET_EVENTS = "wallet.events";
    public static final String RK_BALANCE_CONVERTED = "wallet.event.balance-converted";
    public static final String RK_BALANCE_RELEASED = "wallet.event.balance-released";

    @Bean
    TopicExchange walletEventsExchange() {
        return new TopicExchange(EXCHANGE_WALLET_EVENTS, true, false);
    }

    @Bean
    Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
