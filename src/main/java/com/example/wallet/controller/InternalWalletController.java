package com.example.wallet.controller;

import com.example.wallet.DTO.HoldRequest;
import com.example.wallet.model.Wallet;
import com.example.wallet.service.WalletService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/wallet")
public class InternalWalletController {

    private final WalletService walletService;

    public InternalWalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/hold")
    public Wallet hold(@RequestBody HoldRequest request) {
        return walletService.holdBalance(
                request.getUserId(),
                request.getAmount(),
                request.getAuctId()
        );
    }

    @PostMapping("/release")
    public Wallet release(@RequestBody HoldRequest request) {
        return walletService.releaseBalance(
                request.getUserId(),
                request.getAmount(),
                request.getAuctId()
        );
    }

    @PostMapping("/convert")
    public Wallet convert(@RequestBody HoldRequest request) {
        return walletService.convertToPayment(
                request.getUserId(),
                request.getAmount(),
                request.getAuctId()
        );
    }
}