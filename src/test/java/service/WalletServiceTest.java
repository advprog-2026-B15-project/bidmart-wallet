package service;

import com.example.wallet.event.BalanceHeldEvent;
import com.example.wallet.event.BalanceReleasedEvent;
import com.example.wallet.event.PaymentCompletedEvent;
import com.example.wallet.event.TopUpCompletedEvent;
import com.example.wallet.model.TransactionType;
import com.example.wallet.model.Wallet;
import com.example.wallet.model.WalletTransaction;
import com.example.wallet.repository.WalletRepository;
import com.example.wallet.repository.WalletTransactionRepository;
import com.example.wallet.service.WalletService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WalletServiceTest {

    private WalletRepository walletRepository;
    private WalletTransactionRepository transactionRepository;
    private ApplicationEventPublisher eventPublisher;
    private WalletService walletService;

    @BeforeEach
    void setUp() {
        walletRepository = mock(WalletRepository.class);
        transactionRepository = mock(WalletTransactionRepository.class);
        eventPublisher = mock(ApplicationEventPublisher.class);

        walletService = new WalletService(
                walletRepository,
                transactionRepository,
                eventPublisher
        );
    }

    private Wallet createWallet() {
        Wallet wallet = new Wallet();
        wallet.setId("wallet-1");
        wallet.setUserId("user-1");
        wallet.setAvailableBalance(new BigDecimal("1000"));
        wallet.setHeldBalance(BigDecimal.ZERO);
        return wallet;
    }

    @Test
    void getWallet_WhenWalletExists_ReturnsWallet() {
        Wallet wallet = createWallet();

        when(walletRepository.findByUserId("user-1"))
                .thenReturn(Optional.of(wallet));

        Wallet result = walletService.getWallet("user-1");

        assertEquals("user-1", result.getUserId());
        assertEquals(new BigDecimal("1000"), result.getAvailableBalance());
    }

    @Test
    void getWallet_WhenWalletDoesNotExist_CreatesWallet() {
        when(walletRepository.findByUserId("user-1"))
                .thenReturn(Optional.empty());

        when(walletRepository.save(any(Wallet.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Wallet result = walletService.getWallet("user-1");

        assertEquals("user-1", result.getUserId());
        assertEquals(BigDecimal.ZERO, result.getAvailableBalance());
        assertEquals(BigDecimal.ZERO, result.getHeldBalance());

        verify(walletRepository).save(any(Wallet.class));
    }

    @Test
    void topUp_ShouldIncreaseAvailableBalanceAndSaveTransaction() {
        Wallet wallet = createWallet();

        when(walletRepository.findByUserId("user-1"))
                .thenReturn(Optional.of(wallet));

        walletService.topUp("user-1", new BigDecimal("500"));

        assertEquals(new BigDecimal("1500"), wallet.getAvailableBalance());

        ArgumentCaptor<WalletTransaction> txCaptor =
                ArgumentCaptor.forClass(WalletTransaction.class);

        verify(transactionRepository).save(txCaptor.capture());

        WalletTransaction tx = txCaptor.getValue();

        assertEquals(TransactionType.TOP_UP, tx.getType());
        assertEquals(new BigDecimal("500"), tx.getAmount());
        assertEquals(new BigDecimal("1000"), tx.getBalanceBefore());
        assertEquals(new BigDecimal("1500"), tx.getBalanceAfter());

        verify(eventPublisher).publishEvent(any(TopUpCompletedEvent.class));
    }

    @Test
    void withdraw_ShouldDecreaseAvailableBalanceAndSaveTransaction() {
        Wallet wallet = createWallet();

        when(walletRepository.findByUserId("user-1"))
                .thenReturn(Optional.of(wallet));

        walletService.withdraw("user-1", new BigDecimal("300"));

        assertEquals(new BigDecimal("700"), wallet.getAvailableBalance());

        ArgumentCaptor<WalletTransaction> txCaptor =
                ArgumentCaptor.forClass(WalletTransaction.class);

        verify(transactionRepository).save(txCaptor.capture());

        WalletTransaction tx = txCaptor.getValue();

        assertEquals(TransactionType.WITHDRAW, tx.getType());
        assertEquals(new BigDecimal("300"), tx.getAmount());
        assertEquals(new BigDecimal("1000"), tx.getBalanceBefore());
        assertEquals(new BigDecimal("700"), tx.getBalanceAfter());
    }

    @Test
    void withdraw_WhenInsufficientBalance_ShouldThrowException() {
        Wallet wallet = createWallet();

        when(walletRepository.findByUserId("user-1"))
                .thenReturn(Optional.of(wallet));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> walletService.withdraw("user-1", new BigDecimal("2000"))
        );

        assertEquals("Insufficient balance", exception.getMessage());

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void holdBalance_ShouldMoveAvailableToHeldBalance() {
        Wallet wallet = createWallet();

        when(transactionRepository.existsByAuctIdAndType("auction-1", TransactionType.HOLD))
                .thenReturn(false);

        when(walletRepository.findByUserId("user-1"))
                .thenReturn(Optional.of(wallet));

        walletService.holdBalance("user-1", amount, "auction-1", "auction-1-user-1-hold");

        assertEquals(new BigDecimal("600"), wallet.getAvailableBalance());
        assertEquals(new BigDecimal("400"), wallet.getHeldBalance());

        ArgumentCaptor<WalletTransaction> txCaptor =
                ArgumentCaptor.forClass(WalletTransaction.class);

        verify(transactionRepository).save(txCaptor.capture());

        WalletTransaction tx = txCaptor.getValue();

        assertEquals(TransactionType.HOLD, tx.getType());
        assertEquals("auction-1", tx.getAuctId());
        assertEquals(new BigDecimal("1000"), tx.getBalanceBefore());
        assertEquals(new BigDecimal("600"), tx.getBalanceAfter());

        verify(eventPublisher).publishEvent(any(BalanceHeldEvent.class));
    }

    @Test
    void holdBalance_WhenDuplicateRequest_ShouldReturnWalletWithoutSavingTransaction() {
        Wallet wallet = createWallet();

        when(transactionRepository.existsByAuctIdAndType("auction-1", TransactionType.HOLD))
                .thenReturn(true);

        when(walletRepository.findByUserId("user-1"))
                .thenReturn(Optional.of(wallet));

        Wallet result = walletService.holdBalance("user-1", new BigDecimal("400"), "auction-1");

        assertEquals(wallet, result);

        verify(transactionRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any(BalanceHeldEvent.class));
    }

    @Test
    void releaseBalance_ShouldMoveHeldToAvailableBalance() {
        Wallet wallet = createWallet();
        wallet.setAvailableBalance(new BigDecimal("600"));
        wallet.setHeldBalance(new BigDecimal("400"));

        when(transactionRepository.existsByAuctIdAndType("auction-1", TransactionType.RELEASE))
                .thenReturn(false);

        when(walletRepository.findByUserId("user-1"))
                .thenReturn(Optional.of(wallet));

        walletService.releaseBalance("user-1", amount, "auction-1", "auction-1-user-1-release");

        assertEquals(new BigDecimal("1000"), wallet.getAvailableBalance());
        assertEquals(BigDecimal.ZERO, wallet.getHeldBalance());

        verify(eventPublisher).publishEvent(any(BalanceReleasedEvent.class));
    }

    @Test
    void convertToPayment_ShouldDecreaseHeldBalance() {
        Wallet wallet = createWallet();
        wallet.setAvailableBalance(new BigDecimal("600"));
        wallet.setHeldBalance(new BigDecimal("400"));

        when(transactionRepository.existsByAuctIdAndType("auction-1", TransactionType.PAYMENT))
                .thenReturn(false);

        when(walletRepository.findByUserId("user-1"))
                .thenReturn(Optional.of(wallet));

        walletService.convertToPayment("user-1", amount, "auction-1", "auction-1-user-1-payment");

        assertEquals(new BigDecimal("600"), wallet.getAvailableBalance());
        assertEquals(BigDecimal.ZERO, wallet.getHeldBalance());

        verify(eventPublisher).publishEvent(any(PaymentCompletedEvent.class));
    }

    @Test
    void getTransactions_ShouldReturnPaginatedTransactions() {
        Wallet wallet = createWallet();

        WalletTransaction tx = new WalletTransaction(
                wallet.getId(),
                TransactionType.TOP_UP,
                new BigDecimal("100"),
                null,
                new BigDecimal("0"),
                new BigDecimal("100")
        );

        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<WalletTransaction> page = new PageImpl<>(List.of(tx));

        when(walletRepository.findByUserId("user-1"))
                .thenReturn(Optional.of(wallet));

        when(transactionRepository.findByWalletIdOrderByCreatedAtDesc(wallet.getId(), pageRequest))
                .thenReturn(page);

        Page<WalletTransaction> result = walletService.getTransactions("user-1", pageRequest);

        assertEquals(1, result.getContent().size());
        assertEquals(TransactionType.TOP_UP, result.getContent().get(0).getType());
    }
}