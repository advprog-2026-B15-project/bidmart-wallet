package service;

import com.example.wallet.repository.WalletRepository;
import com.example.wallet.repository.WalletTransactionRepository;
import com.example.wallet.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WalletServiceValidationTest {

    private WalletRepository walletRepository;
    private WalletTransactionRepository transactionRepository;
    private ApplicationEventPublisher eventPublisher;
    private WalletService walletService;

    @BeforeEach
    void setUp() {
        walletRepository = mock(WalletRepository.class);
        transactionRepository = mock(WalletTransactionRepository.class);
        eventPublisher = mock(ApplicationEventPublisher.class);
        walletService = new WalletService(walletRepository, transactionRepository, eventPublisher);
    }

    // --- validateAmount ---

    @Test
    void topUp_WithNullAmount_ShouldThrow() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> walletService.topUp("user-1", null));
        assertEquals("Amount must be greater than zero", ex.getMessage());
    }

    @Test
    void topUp_WithZeroAmount_ShouldThrow() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> walletService.topUp("user-1", BigDecimal.ZERO));
        assertEquals("Amount must be greater than zero", ex.getMessage());
    }

    @Test
    void topUp_WithNegativeAmount_ShouldThrow() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> walletService.topUp("user-1", new BigDecimal("-1")));
        assertEquals("Amount must be greater than zero", ex.getMessage());
    }

    @Test
    void withdraw_WithNullAmount_ShouldThrow() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> walletService.withdraw("user-1", null));
        assertEquals("Amount must be greater than zero", ex.getMessage());
    }

    @Test
    void withdraw_WithZeroAmount_ShouldThrow() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> walletService.withdraw("user-1", BigDecimal.ZERO));
        assertEquals("Amount must be greater than zero", ex.getMessage());
    }

    // --- validateIdempotencyKey ---

    @Test
    void holdBalance_WithNullIdempotencyKey_ShouldThrow() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> walletService.holdBalance("user-1", new BigDecimal("100"), "auction-1", null));
        assertEquals("Idempotency key is required", ex.getMessage());
    }

    @Test
    void holdBalance_WithBlankIdempotencyKey_ShouldThrow() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> walletService.holdBalance("user-1", new BigDecimal("100"), "auction-1", "  "));
        assertEquals("Idempotency key is required", ex.getMessage());
    }

    @Test
    void releaseBalance_WithNullIdempotencyKey_ShouldThrow() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> walletService.releaseBalance("user-1", new BigDecimal("100"), "auction-1", null));
        assertEquals("Idempotency key is required", ex.getMessage());
    }

    @Test
    void releaseBalance_WithBlankIdempotencyKey_ShouldThrow() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> walletService.releaseBalance("user-1", new BigDecimal("100"), "auction-1", ""));
        assertEquals("Idempotency key is required", ex.getMessage());
    }

    @Test
    void convertToPayment_WithNullIdempotencyKey_ShouldThrow() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> walletService.convertToPayment("user-1", new BigDecimal("100"), "auction-1", null));
        assertEquals("Idempotency key is required", ex.getMessage());
    }

    @Test
    void convertToPayment_WithBlankIdempotencyKey_ShouldThrow() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> walletService.convertToPayment("user-1", new BigDecimal("100"), "auction-1", ""));
        assertEquals("Idempotency key is required", ex.getMessage());
    }
}
