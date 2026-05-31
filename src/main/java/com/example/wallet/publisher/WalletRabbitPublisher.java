package com.example.wallet.publisher;

import com.example.wallet.config.WalletRabbitConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class WalletRabbitPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishBalanceConverted(String userId, String auctId, BigDecimal amount) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", userId);
        payload.put("auctionId", auctId);
        payload.put("amount", amount.longValue());
        payload.put("currency", "IDR");
        payload.put("conversionReference", auctId + "-" + userId + "-convert");

        Map<String, Object> event = buildEnvelope("BalanceConverted", payload);

        try {
            rabbitTemplate.convertAndSend(
                    WalletRabbitConfig.EXCHANGE_WALLET_EVENTS,
                    WalletRabbitConfig.RK_BALANCE_CONVERTED,
                    event
            );
            log.info("Published BalanceConverted for userId={} auctId={}", userId, auctId);
        } catch (Exception e) {
            log.error("Failed to publish BalanceConverted for userId={} auctId={}: {}", userId, auctId, e.getMessage());
        }
    }

    public void publishBalanceReleased(String userId, String auctId, BigDecimal amount) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", userId);
        payload.put("auctionId", auctId);
        payload.put("amount", amount.longValue());
        payload.put("currency", "IDR");
        payload.put("releaseReference", auctId + "-" + userId + "-release");

        Map<String, Object> event = buildEnvelope("BalanceReleased", payload);

        try {
            rabbitTemplate.convertAndSend(
                    WalletRabbitConfig.EXCHANGE_WALLET_EVENTS,
                    WalletRabbitConfig.RK_BALANCE_RELEASED,
                    event
            );
            log.info("Published BalanceReleased for userId={} auctId={}", userId, auctId);
        } catch (Exception e) {
            log.error("Failed to publish BalanceReleased for userId={} auctId={}: {}", userId, auctId, e.getMessage());
        }
    }

    private Map<String, Object> buildEnvelope(String eventType, Map<String, Object> payload) {
        Map<String, Object> envelope = new HashMap<>();
        envelope.put("eventId", UUID.randomUUID().toString());
        envelope.put("eventType", eventType);
        envelope.put("eventVersion", 1);
        envelope.put("occurredAt", OffsetDateTime.now(ZoneOffset.UTC).toString());
        envelope.put("source", "bidmart-wallet");
        envelope.put("payload", payload);
        return envelope;
    }
}
