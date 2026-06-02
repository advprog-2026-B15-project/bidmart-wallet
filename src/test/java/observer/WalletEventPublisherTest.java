package observer;

import com.example.wallet.observer.WalletEvent;
import com.example.wallet.observer.WalletEventObserver;
import com.example.wallet.observer.WalletEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

class WalletEventPublisherTest {

    private WalletEventPublisher publisher;

    @BeforeEach
    void setUp() {
        publisher = new WalletEventPublisher();
    }

    @Test
    void registerObserver_ShouldReceiveNotifications() {
        WalletEventObserver observer = mock(WalletEventObserver.class);
        WalletEvent event = new WalletEvent(
                WalletEvent.Type.TOP_UP_COMPLETED, "user-1", new BigDecimal("100"), null);

        publisher.registerObserver(observer);
        publisher.notifyObservers(event);

        verify(observer).onEvent(event);
    }

    @Test
    void removeObserver_ShouldNoLongerReceiveNotifications() {
        WalletEventObserver observer = mock(WalletEventObserver.class);
        WalletEvent event = new WalletEvent(
                WalletEvent.Type.BALANCE_HELD, "user-1", new BigDecimal("100"), "auction-1");

        publisher.registerObserver(observer);
        publisher.removeObserver(observer);
        publisher.notifyObservers(event);

        verify(observer, never()).onEvent(any());
    }

    @Test
    void notifyObservers_ShouldNotifyAllRegisteredObservers() {
        WalletEventObserver observer1 = mock(WalletEventObserver.class);
        WalletEventObserver observer2 = mock(WalletEventObserver.class);
        WalletEvent event = new WalletEvent(
                WalletEvent.Type.BALANCE_RELEASED, "user-1", new BigDecimal("50"), "auction-1");

        publisher.registerObserver(observer1);
        publisher.registerObserver(observer2);
        publisher.notifyObservers(event);

        verify(observer1).onEvent(event);
        verify(observer2).onEvent(event);
    }

    @Test
    void notifyObservers_WhenObserverThrows_ShouldContinueNotifyingOthers() {
        WalletEventObserver failingObserver = mock(WalletEventObserver.class);
        WalletEventObserver healthyObserver = mock(WalletEventObserver.class);
        WalletEvent event = new WalletEvent(
                WalletEvent.Type.PAYMENT_COMPLETED, "user-1", new BigDecimal("100"), "auction-1");

        doThrow(new RuntimeException("observer error")).when(failingObserver).onEvent(any());

        publisher.registerObserver(failingObserver);
        publisher.registerObserver(healthyObserver);
        publisher.notifyObservers(event);

        // healthy observer still receives the event despite the first one failing
        verify(healthyObserver).onEvent(event);
    }

    @Test
    void notifyObservers_WithNoObservers_ShouldNotThrow() {
        WalletEvent event = new WalletEvent(
                WalletEvent.Type.TOP_UP_COMPLETED, "user-1", new BigDecimal("100"), null);

        // should complete without exception
        publisher.notifyObservers(event);
    }
}
