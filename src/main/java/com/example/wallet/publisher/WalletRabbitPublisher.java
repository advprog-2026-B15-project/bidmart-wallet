package com.example.wallet.publisher;

import com.example.wallet.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class WalletRabbitPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishBalanceReleased(String userId, String auctionId, BigDecimal amount) {
        Map<String, Object> message = Map.of(
                "userId", userId,
                "auctionId", auctionId,
                "amount", amount
        );

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.WALLET_EVENTS_EXCHANGE,
                RabbitMQConfig.BALANCE_RELEASED_ROUTING_KEY,
                message
        );

        log.info("Published balance-released event: userId={}, auctionId={}, amount={}",
                userId, auctionId, amount);
    }

    public void publishBalanceConverted(String userId, String auctionId, BigDecimal amount) {
        Map<String, Object> message = Map.of(
                "userId", userId,
                "auctionId", auctionId,
                "amount", amount
        );

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.WALLET_EVENTS_EXCHANGE,
                RabbitMQConfig.PAYMENT_COMPLETED_ROUTING_KEY,
                message
        );

        log.info("Published payment-completed event: userId={}, auctionId={}, amount={}",
                userId, auctionId, amount);
    }
}
