package consumer;

import com.example.wallet.DTO.HoldRequest;
import com.example.wallet.consumer.WalletRabbitConsumer;
import com.example.wallet.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

class WalletRabbitConsumerTest {

    private WalletService walletService;
    private WalletRabbitConsumer consumer;

    @BeforeEach
    void setUp() {
        walletService = mock(WalletService.class);
        consumer = new WalletRabbitConsumer(walletService);
    }

    private HoldRequest createRequest() {
        HoldRequest request = new HoldRequest();
        request.setUserId("user-1");
        request.setAmount(new BigDecimal("100"));
        request.setAuctId("auction-1");
        request.setIdempotencyKey("key-1");
        return request;
    }

    @Test
    void handleHold_ShouldCallWalletService() {
        HoldRequest request = createRequest();

        consumer.handleHold(request);

        verify(walletService).holdBalance("user-1", new BigDecimal("100"), "auction-1", "key-1");
    }

    @Test
    void handleHold_WhenServiceThrows_ShouldNotPropagate() {
        HoldRequest request = createRequest();

        doThrow(new RuntimeException("Insufficient balance"))
                .when(walletService).holdBalance(any(), any(), any(), any());

        // should not throw — errors are logged, message is acked
        consumer.handleHold(request);

        verify(walletService).holdBalance("user-1", new BigDecimal("100"), "auction-1", "key-1");
    }

    @Test
    void handleRelease_ShouldCallWalletService() {
        HoldRequest request = createRequest();

        consumer.handleRelease(request);

        verify(walletService).releaseBalance("user-1", new BigDecimal("100"), "auction-1", "key-1");
    }

    @Test
    void handleRelease_WhenServiceThrows_ShouldNotPropagate() {
        HoldRequest request = createRequest();

        doThrow(new RuntimeException("Insufficient held balance"))
                .when(walletService).releaseBalance(any(), any(), any(), any());

        consumer.handleRelease(request);

        verify(walletService).releaseBalance("user-1", new BigDecimal("100"), "auction-1", "key-1");
    }

    @Test
    void handleConvert_ShouldCallWalletService() {
        HoldRequest request = createRequest();

        consumer.handleConvert(request);

        verify(walletService).convertToPayment("user-1", new BigDecimal("100"), "auction-1", "key-1");
    }

    @Test
    void handleConvert_WhenServiceThrows_ShouldNotPropagate() {
        HoldRequest request = createRequest();

        doThrow(new RuntimeException("Insufficient held balance"))
                .when(walletService).convertToPayment(any(), any(), any(), any());

        consumer.handleConvert(request);

        verify(walletService).convertToPayment("user-1", new BigDecimal("100"), "auction-1", "key-1");
    }
}
