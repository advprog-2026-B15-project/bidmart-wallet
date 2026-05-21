package controller;

import com.example.wallet.DTO.AmountRequest;
import com.example.wallet.controller.WalletController;
import com.example.wallet.model.Wallet;
import com.example.wallet.model.WalletTransaction;
import com.example.wallet.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WalletControllerTest {

    private WalletService walletService;
    private WalletController walletController;

    @BeforeEach
    void setUp() {
        walletService = mock(WalletService.class);
        walletController = new WalletController(walletService);
    }

    @Test
    void getWallet_ShouldReturnWallet() {
        Wallet wallet = new Wallet();
        wallet.setUserId("user-1");

        when(walletService.getWallet("user-1"))
                .thenReturn(wallet);

        Wallet result = walletController.getWallet("user-1");

        assertEquals("user-1", result.getUserId());
    }

    @Test
    void topUp_ShouldCallService() {
        AmountRequest request = new AmountRequest();
        request.setAmount(new BigDecimal("100"));

        Wallet wallet = new Wallet();
        wallet.setUserId("user-1");

        when(walletService.topUp("user-1", new BigDecimal("100")))
                .thenReturn(wallet);

        Wallet result = walletController.topUp("user-1", request);

        assertEquals("user-1", result.getUserId());
    }

    @Test
    void withdraw_ShouldCallService() {
        AmountRequest request = new AmountRequest();
        request.setAmount(new BigDecimal("100"));

        Wallet wallet = new Wallet();
        wallet.setUserId("user-1");

        when(walletService.withdraw("user-1", new BigDecimal("100")))
                .thenReturn(wallet);

        Wallet result = walletController.withdraw("user-1", request);

        assertEquals("user-1", result.getUserId());
    }

    @Test
    void transactions_ShouldReturnPage() {
        Page<WalletTransaction> page = new PageImpl<>(List.of());

        when(walletService.getTransactions(eq("user-1"), any()))
                .thenReturn(page);

        Page<WalletTransaction> result =
                walletController.transactions("user-1", 0, 10);

        assertEquals(0, result.getContent().size());
    }
}