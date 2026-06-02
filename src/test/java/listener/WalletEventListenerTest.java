package listener;

import com.example.wallet.listener.WalletEventListener;
import com.example.wallet.observer.WalletEvent;
import com.example.wallet.observer.WalletEventPublisher;
import com.example.wallet.publisher.WalletRabbitPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

class WalletEventListenerTest {

    private WalletRabbitPublisher rabbitPublisher;
    private WalletEventPublisher walletEventPublisher;
    private WalletEventListener listener;

    @BeforeEach
    void setUp() {
        rabbitPublisher = mock(WalletRabbitPublisher.class);
        walletEventPublisher = mock(WalletEventPublisher.class);
        listener = new WalletEventListener(rabbitPublisher, walletEventPublisher);
    }

    @Test
    void register_ShouldRegisterWithPublisher() {
        listener.register();
        verify(walletEventPublisher).registerObserver(listener);
    }

    @Test
    void onEvent_BalanceReleased_ShouldPublishToRabbit() {
        WalletEvent event = new WalletEvent(
                WalletEvent.Type.BALANCE_RELEASED, "user-1", new BigDecimal("100"), "auction-1");

        listener.onEvent(event);

        verify(rabbitPublisher).publishBalanceReleased("user-1", "auction-1", new BigDecimal("100"));
    }

    @Test
    void onEvent_PaymentCompleted_ShouldPublishToRabbit() {
        WalletEvent event = new WalletEvent(
                WalletEvent.Type.PAYMENT_COMPLETED, "user-1", new BigDecimal("200"), "auction-1");

        listener.onEvent(event);

        verify(rabbitPublisher).publishBalanceConverted("user-1", "auction-1", new BigDecimal("200"));
    }

    @Test
    void onEvent_BalanceHeld_ShouldNotPublishToRabbit() {
        WalletEvent event = new WalletEvent(
                WalletEvent.Type.BALANCE_HELD, "user-1", new BigDecimal("100"), "auction-1");

        listener.onEvent(event);

        verifyNoInteractions(rabbitPublisher);
    }

    @Test
    void onEvent_TopUpCompleted_ShouldNotPublishToRabbit() {
        WalletEvent event = new WalletEvent(
                WalletEvent.Type.TOP_UP_COMPLETED, "user-1", new BigDecimal("50"), null);

        listener.onEvent(event);

        verifyNoInteractions(rabbitPublisher);
    }
}
