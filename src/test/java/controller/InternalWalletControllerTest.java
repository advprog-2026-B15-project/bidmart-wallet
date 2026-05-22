package controller;

import com.example.wallet.DTO.HoldRequest;
import com.example.wallet.controller.InternalWalletController;
import com.example.wallet.model.Wallet;
import com.example.wallet.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InternalWalletControllerTest {

    private WalletService walletService;
    private InternalWalletController internalWalletController;

    @BeforeEach
    void setUp() {
        walletService = mock(WalletService.class);
        internalWalletController = new InternalWalletController(walletService);
    }

    private HoldRequest createRequest() {
        HoldRequest request = new HoldRequest();
        request.setUserId("user-1");
        request.setAmount(new BigDecimal("100"));
        request.setAuctId("auction-1");
        request.setIdempotencyKey("auction-1-user-1-key");
        return request;
    }

    @Test
    void hold_ShouldCallService() {
        HoldRequest request = createRequest();

        Wallet wallet = new Wallet();
        wallet.setUserId("user-1");

        when(walletService.holdBalance("user-1", new BigDecimal("100"), "auction-1", "auction-1-user-1-key"))
                .thenReturn(wallet);

        Wallet result = internalWalletController.hold(request);

        assertEquals("user-1", result.getUserId());
    }

    @Test
    void release_ShouldCallService() {
        HoldRequest request = createRequest();

        Wallet wallet = new Wallet();
        wallet.setUserId("user-1");

        when(walletService.releaseBalance("user-1", new BigDecimal("100"), "auction-1", "auction-1-user-1-key"))
                .thenReturn(wallet);

        Wallet result = internalWalletController.release(request);

        assertEquals("user-1", result.getUserId());
    }

    @Test
    void convert_ShouldCallService() {
        HoldRequest request = createRequest();

        Wallet wallet = new Wallet();
        wallet.setUserId("user-1");

        when(walletService.convertToPayment("user-1", new BigDecimal("100"), "auction-1", "auction-1-user-1-key"))
                .thenReturn(wallet);

        Wallet result = internalWalletController.convert(request);

        assertEquals("user-1", result.getUserId());
    }
}
