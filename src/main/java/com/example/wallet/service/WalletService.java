package com.example.wallet.service;

import com.example.wallet.model.*;
import com.example.wallet.observer.WalletEvent;
import com.example.wallet.observer.WalletEventPublisher;
import com.example.wallet.repository.WalletRepository;
import com.example.wallet.repository.WalletTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;
    private final WalletEventPublisher walletEventPublisher;

    public WalletService(WalletRepository walletRepository,
                         WalletTransactionRepository transactionRepository,
                         WalletEventPublisher walletEventPublisher) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.walletEventPublisher = walletEventPublisher;
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
        validateAmount(amount);
        Wallet wallet = getWallet(userId);

        BigDecimal before = wallet.getAvailableBalance();
        BigDecimal after = before.add(amount);

        wallet.setAvailableBalance(after);
        walletRepository.save(wallet);

        transactionRepository.save(new WalletTransaction(
                wallet.getId(), TransactionType.TOP_UP, amount, null, null, before, after
        ));

        walletEventPublisher.notifyObservers(
                new WalletEvent(WalletEvent.Type.TOP_UP_COMPLETED, userId, amount, null)
        );

        return wallet;
    }

    @Transactional
    public Wallet withdraw(String userId, BigDecimal amount) {
        validateAmount(amount);
        Wallet wallet = getWallet(userId);

        if (wallet.getAvailableBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        BigDecimal before = wallet.getAvailableBalance();
        BigDecimal after = before.subtract(amount);

        wallet.setAvailableBalance(after);
        walletRepository.save(wallet);

        transactionRepository.save(new WalletTransaction(
                wallet.getId(), TransactionType.WITHDRAW, amount, null, null, before, after
        ));

        return wallet;
    }

    @Transactional(readOnly = true)
    public Page<WalletTransaction> getTransactions(String userId, Pageable pageable) {
        Wallet wallet = getWallet(userId);
        return transactionRepository.findByWalletIdOrderByCreatedAtDesc(wallet.getId(), pageable);
    }

    @Transactional
    public Wallet holdBalance(String userId, BigDecimal amount, String auctId, String idempotencyKey) {
        validateAmount(amount);
        validateIdempotencyKey(idempotencyKey);

        if (transactionRepository.existsByIdempotencyKeyAndType(idempotencyKey, TransactionType.HOLD)) {
            return getWallet(userId);
        }

        Wallet wallet = getWallet(userId);

        if (wallet.getAvailableBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        BigDecimal before = wallet.getAvailableBalance();
        BigDecimal after = before.subtract(amount);

        wallet.setAvailableBalance(after);
        wallet.setHeldBalance(wallet.getHeldBalance().add(amount));
        walletRepository.save(wallet);

        transactionRepository.save(new WalletTransaction(
                wallet.getId(), TransactionType.HOLD, amount, auctId, idempotencyKey, before, after
        ));

        walletEventPublisher.notifyObservers(
                new WalletEvent(WalletEvent.Type.BALANCE_HELD, userId, amount, auctId)
        );

        return wallet;
    }

    @Transactional
    public Wallet releaseBalance(String userId, BigDecimal amount, String auctId, String idempotencyKey) {
        validateAmount(amount);
        validateIdempotencyKey(idempotencyKey);

        if (transactionRepository.existsByIdempotencyKeyAndType(idempotencyKey, TransactionType.RELEASE)) {
            return getWallet(userId);
        }

        Wallet wallet = getWallet(userId);

        if (wallet.getHeldBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient held balance");
        }

        BigDecimal before = wallet.getHeldBalance();
        BigDecimal after = before.subtract(amount);

        wallet.setHeldBalance(after);
        wallet.setAvailableBalance(wallet.getAvailableBalance().add(amount));
        walletRepository.save(wallet);

        transactionRepository.save(new WalletTransaction(
                wallet.getId(), TransactionType.RELEASE, amount, auctId, idempotencyKey, before, after
        ));

        walletEventPublisher.notifyObservers(
                new WalletEvent(WalletEvent.Type.BALANCE_RELEASED, userId, amount, auctId)
        );

        return wallet;
    }

    @Transactional
    public Wallet convertToPayment(String userId, BigDecimal amount, String auctId, String idempotencyKey) {
        validateAmount(amount);
        validateIdempotencyKey(idempotencyKey);

        if (transactionRepository.existsByIdempotencyKeyAndType(idempotencyKey, TransactionType.PAYMENT)) {
            return getWallet(userId);
        }

        Wallet wallet = getWallet(userId);

        if (wallet.getHeldBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient held balance");
        }

        BigDecimal before = wallet.getHeldBalance();
        BigDecimal after = before.subtract(amount);

        wallet.setHeldBalance(after);
        walletRepository.save(wallet);

        transactionRepository.save(new WalletTransaction(
                wallet.getId(), TransactionType.PAYMENT, amount, auctId, idempotencyKey, before, after
        ));

        walletEventPublisher.notifyObservers(
                new WalletEvent(WalletEvent.Type.PAYMENT_COMPLETED, userId, amount, auctId)
        );

        return wallet;
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be greater than zero");
        }
    }

    private void validateIdempotencyKey(String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new RuntimeException("Idempotency key is required");
        }
    }
}
