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

    public Wallet getWallet(String user_id) {
        return walletRepository.findByUserId(user_id)
                .orElseGet(() -> createWallet(user_id));
    }

    public Wallet createWallet(String user_id) {
        Wallet wallet = new Wallet();
        wallet.setUserId(user_id);
        return walletRepository.save(wallet);
    }

    @Transactional
    public Wallet topUp(String user_id, BigDecimal amount) {

        Wallet wallet = getWallet(user_id);

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
    public Wallet withdraw(String user_id, BigDecimal amount) {

        Wallet wallet = getWallet(user_id);

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

    public List<WalletTransaction> getTransactions(String user_id) {

        Wallet wallet = getWallet(user_id);

        return transactionRepository.findByWalletId(wallet.getId());
    }

    @Transactional
    public Wallet holdBalance(String user_id, BigDecimal amount, String auct_id) {

        //check if already processed for idempotentcy
        if (transactionRepository.existsByAuctId(auct_id)) {
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
                auct_id
        ));

        return wallet;
    }

    @Transactional
    public Wallet releaseBalance(String userId, BigDecimal amount, String auct_id) {

        //check if already processed for idempotentcy
        if (transactionRepository.existsByAuctId(auct_id)) {
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
                auct_id
        ));

        return wallet;
    }

    @Transactional
    public Wallet convertToPayment(String user_id, BigDecimal amount, String auct_id) {

        if (transactionRepository.existsByAuctId(auct_id)) {
            return getWallet(user_id);
        }

        Wallet wallet = getWallet(user_id);

        if (wallet.getHeldBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient held balance");
        }

        wallet.setHeldBalance(wallet.getHeldBalance().subtract(amount));

        walletRepository.save(wallet);

        transactionRepository.save(new WalletTransaction(
                wallet.getId(),
                TransactionType.PAYMENT,
                amount,
                auct_id
        ));

        return wallet;
    }
}

