package com.example.wallet.service;

import com.example.wallet.model.Wallet;
import com.example.wallet.model.WalletTransaction;
import com.example.wallet.repository.WalletRepository;
import com.example.wallet.repository.WalletTransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;

    public WalletService(WalletRepository walletRepository,WalletTransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    public Wallet getWallet(String userId) {
        return walletRepository.findByUserId(userId)
                .orElseGet(() -> createWallet(userId));
    }

    public Wallet createWallet(String userId) {
        Wallet wallet = new Wallet();
        wallet.setUserId(userId);
        return walletRepository.save(wallet);
    }
}
