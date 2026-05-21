package event;

import org.junit.jupiter.api.Test;

import com.example.wallet.event.*;


import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class WalletEventTest {

    @Test
    void balanceHeldEvent_ShouldStoreValues() {
        BalanceHeldEvent event = new BalanceHeldEvent(
                "user-1",
                new BigDecimal("100"),
                "auction-1"
        );

        assertEquals("user-1", event.getUserId());
        assertEquals(new BigDecimal("100"), event.getAmount());
        assertEquals("auction-1", event.getAuctionId());
    }

    @Test
    void balanceReleasedEvent_ShouldStoreValues() {
        BalanceReleasedEvent event = new BalanceReleasedEvent(
                "user-1",
                new BigDecimal("100"),
                "auction-1"
        );

        assertEquals("user-1", event.getUserId());
        assertEquals(new BigDecimal("100"), event.getAmount());
        assertEquals("auction-1", event.getAuctionId());
    }

    @Test
    void paymentCompletedEvent_ShouldStoreValues() {
        PaymentCompletedEvent event = new PaymentCompletedEvent(
                "user-1",
                new BigDecimal("100"),
                "auction-1"
        );

        assertEquals("user-1", event.getUserId());
        assertEquals(new BigDecimal("100"), event.getAmount());
        assertEquals("auction-1", event.getAuctionId());
    }

    @Test
    void topUpCompletedEvent_ShouldStoreValues() {
        TopUpCompletedEvent event = new TopUpCompletedEvent(
                "user-1",
                new BigDecimal("100")
        );

        assertEquals("user-1", event.getUserId());
        assertEquals(new BigDecimal("100"), event.getAmount());
    }
}