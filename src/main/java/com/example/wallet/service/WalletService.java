package com.example.wallet.service;

import com.example.wallet.model.TransactionType;
import com.example.wallet.model.Wallet;
import com.example.wallet.model.WalletTransaction;
import com.example.wallet.repository.WalletRepository;
import com.example.wallet.repository.WalletTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public Wallet topUp(String userId, BigDecimal amount) {

        Wallet wallet = getWallet(userId);

        wallet.setAvailableBalance(
                wallet.getAvailableBalance().add(amount)
        );

        walletRepository.save(wallet);

        WalletTransaction tx = new WalletTransaction(
                wallet.getId(),
                TransactionType.TOP_UP,
                amount,
                null
        );

        transactionRepository.save(tx);

        return wallet;
    }

    @Transactional
    public Wallet withdraw(String userId, BigDecimal amount) {

        Wallet wallet = getWallet(userId);

        if (wallet.getAvailableBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        wallet.setAvailableBalance(
                wallet.getAvailableBalance().subtract(amount)
        );

        walletRepository.save(wallet);

        WalletTransaction tx = new WalletTransaction(
                wallet.getId(),
                TransactionType.WITHDRAW,
                amount,
                null
        );

        transactionRepository.save(tx);

        return wallet;
    }

    public List<WalletTransaction> getTransactions(String userId) {

        Wallet wallet = getWallet(userId);

        return transactionRepository.findByWalletId(wallet.getId());
    }

    @Transactional
    public Wallet holdBalance(String user_id, BigDecimal amount, String referenceId) {

        //check if already processed for idempotentcy
        if (transactionRepository.existsByReferenceId(referenceId)) {
            return getWallet(user_id);
        }

        Wallet wallet = getWallet(user_id);

        if (wallet.getAvailableBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        wallet.setAvailableBalance(wallet.getAvailableBalance().subtract(amount));
        wallet.setHeldBalance(wallet.getHeldBalance().add(amount));

        walletRepository.save(wallet);

        transactionRepository.save(new WalletTransaction(
                wallet.getId(),
                TransactionType.HOLD,
                amount,
                referenceId
        ));

        return wallet;
    }

    @Transactional
    public Wallet releaseBalance(String userId, BigDecimal amount, String referenceId) {

        //check if already processed for idempotentcy
        if (transactionRepository.existsByReferenceId(referenceId)) {
            return getWallet(userId);
        }

        Wallet wallet = getWallet(userId);

        if (wallet.getHeldBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient held balance");
        }

        wallet.setHeldBalance(wallet.getHeldBalance().subtract(amount));
        wallet.setAvailableBalance(wallet.getAvailableBalance().add(amount));

        walletRepository.save(wallet);

        transactionRepository.save(new WalletTransaction(
                wallet.getId(),
                TransactionType.RELEASE,
                amount,
                referenceId
        ));

        return wallet;
    }

    @Transactional
    public Wallet convertToPayment(String userId, BigDecimal amount, String referenceId) {

        if (transactionRepository.existsByReferenceId(referenceId)) {
            return getWallet(userId);
        }

        Wallet wallet = getWallet(userId);

        if (wallet.getHeldBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient held balance");
        }

        wallet.setHeldBalance(wallet.getHeldBalance().subtract(amount));

        walletRepository.save(wallet);

        transactionRepository.save(new WalletTransaction(
                wallet.getId(),
                TransactionType.PAYMENT,
                amount,
                referenceId
        ));

        return wallet;
    }
}

