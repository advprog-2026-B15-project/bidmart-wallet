package publisher;

import com.example.wallet.config.RabbitMQConfig;
import com.example.wallet.publisher.WalletRabbitPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WalletRabbitPublisherTest {

    private RabbitTemplate rabbitTemplate;
    private WalletRabbitPublisher publisher;

    @BeforeEach
    void setUp() {
        rabbitTemplate = mock(RabbitTemplate.class);
        publisher = new WalletRabbitPublisher(rabbitTemplate);
    }

    @Test
    void publishBalanceReleased_ShouldSendToCorrectExchangeAndRoutingKey() {
        publisher.publishBalanceReleased("user-1", "auction-1", new BigDecimal("100"));

        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMQConfig.WALLET_EVENTS_EXCHANGE),
                eq(RabbitMQConfig.BALANCE_RELEASED_ROUTING_KEY),
                any(Map.class)
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    void publishBalanceReleased_ShouldIncludeCorrectPayload() {
        ArgumentCaptor<Object> messageCaptor = ArgumentCaptor.forClass(Object.class);

        publisher.publishBalanceReleased("user-1", "auction-1", new BigDecimal("100"));

        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMQConfig.WALLET_EVENTS_EXCHANGE),
                eq(RabbitMQConfig.BALANCE_RELEASED_ROUTING_KEY),
                messageCaptor.capture()
        );

        Map<String, Object> payload = (Map<String, Object>) messageCaptor.getValue();
        assertEquals("user-1", payload.get("userId"));
        assertEquals("auction-1", payload.get("auctionId"));
        assertEquals(new BigDecimal("100"), payload.get("amount"));
    }

    @Test
    void publishBalanceConverted_ShouldSendToCorrectExchangeAndRoutingKey() {
        publisher.publishBalanceConverted("user-1", "auction-1", new BigDecimal("200"));

        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMQConfig.WALLET_EVENTS_EXCHANGE),
                eq(RabbitMQConfig.PAYMENT_COMPLETED_ROUTING_KEY),
                any(Map.class)
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    void publishBalanceConverted_ShouldIncludeCorrectPayload() {
        ArgumentCaptor<Object> messageCaptor = ArgumentCaptor.forClass(Object.class);

        publisher.publishBalanceConverted("user-2", "auction-2", new BigDecimal("300"));

        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMQConfig.WALLET_EVENTS_EXCHANGE),
                eq(RabbitMQConfig.PAYMENT_COMPLETED_ROUTING_KEY),
                messageCaptor.capture()
        );

        Map<String, Object> payload = (Map<String, Object>) messageCaptor.getValue();
        assertEquals("user-2", payload.get("userId"));
        assertEquals("auction-2", payload.get("auctionId"));
        assertEquals(new BigDecimal("300"), payload.get("amount"));
    }
}
