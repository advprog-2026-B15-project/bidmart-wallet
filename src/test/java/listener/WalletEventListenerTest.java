package listener;

import com.example.wallet.event.BalanceHeldEvent;
import com.example.wallet.event.BalanceReleasedEvent;
import com.example.wallet.event.PaymentCompletedEvent;
import com.example.wallet.event.TopUpCompletedEvent;
import com.example.wallet.listener.WalletEventListener;
import com.example.wallet.publisher.WalletRabbitPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

class WalletEventListenerTest {

    private WalletRabbitPublisher rabbitPublisher;
    private WalletEventListener listener;

    @BeforeEach
    void setUp() {
        rabbitPublisher = mock(WalletRabbitPublisher.class);
        listener = new WalletEventListener(rabbitPublisher);
    }

    @Test
    void handleBalanceHeld_ShouldNotPublishToRabbit() {
        BalanceHeldEvent event = new BalanceHeldEvent("user-1", new BigDecimal("100"), "auction-1");

        listener.handleBalanceHeld(event);

        verifyNoInteractions(rabbitPublisher);
    }

    @Test
    void handleBalanceReleased_ShouldPublishBalanceReleased() {
        BalanceReleasedEvent event = new BalanceReleasedEvent("user-1", new BigDecimal("100"), "auction-1");

        listener.handleBalanceReleased(event);

        verify(rabbitPublisher).publishBalanceReleased("user-1", "auction-1", new BigDecimal("100"));
    }

    @Test
    void handlePaymentCompleted_ShouldPublishBalanceConverted() {
        PaymentCompletedEvent event = new PaymentCompletedEvent("user-1", new BigDecimal("200"), "auction-1");

        listener.handlePaymentCompleted(event);

        verify(rabbitPublisher).publishBalanceConverted("user-1", "auction-1", new BigDecimal("200"));
    }

    @Test
    void handleTopUpCompleted_ShouldNotPublishToRabbit() {
        TopUpCompletedEvent event = new TopUpCompletedEvent("user-1", new BigDecimal("50"));

        listener.handleTopUpCompleted(event);

        verifyNoInteractions(rabbitPublisher);
    }
}
