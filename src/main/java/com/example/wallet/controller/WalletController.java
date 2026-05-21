package com.example.wallet.controller;

import com.example.wallet.DTO.AmountRequest;
import com.example.wallet.model.Wallet;
import com.example.wallet.model.WalletTransaction;
import com.example.wallet.service.WalletService;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import java.util.List;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {
    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/{userId}")
    public Wallet getWallet(@PathVariable String userId) {
        return walletService.getWallet(userId);
    }

    @PostMapping("/{userId}/topup")
    public Wallet topUp(@PathVariable String userId,
                        @RequestBody AmountRequest request) {

        return walletService.topUp(userId, request.getAmount());
    }
    @PostMapping("/{userId}/withdraw")
    public Wallet withdraw(@PathVariable String userId,
                           @RequestBody AmountRequest request) {

        return walletService.withdraw(userId, request.getAmount());
    }

    //loads transaction by 20 transactions per API call
    @GetMapping("/{userId}/transactions")
    public Page<WalletTransaction> transactions(
        @PathVariable String userId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
        ) {
        return walletService.getTransactions(userId, PageRequest.of(page, size));
    }

}
