package com.example.wallet.service;

import com.example.wallet.event.*;
import com.example.wallet.model.*;
import com.example.wallet.repository.WalletRepository;
import com.example.wallet.repository.WalletTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;
    private final ApplicationEventPublisher eventPublisher;

    public WalletService(WalletRepository walletRepository, WalletTransactionRepository transactionRepository,
            ApplicationEventPublisher eventPublisher) {

        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.eventPublisher = eventPublisher;
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
        validateAmount(amount);
        Wallet wallet = getWallet(user_id);

        BigDecimal before = wallet.getAvailableBalance();
        BigDecimal after = before.add(amount);

        wallet.setAvailableBalance(after);

        walletRepository.save(wallet);

        WalletTransaction tx = new WalletTransaction(
                wallet.getId(),
                TransactionType.TOP_UP,
                amount,
                null,
                before,
                after
        );

        transactionRepository.save(tx);

        eventPublisher.publishEvent(
                new TopUpCompletedEvent(user_id, amount)
        );

        return wallet;
    }

    @Transactional
    public Wallet withdraw(String user_id, BigDecimal amount) {
        validateAmount(amount);
        Wallet wallet = getWallet(user_id);

        if (wallet.getAvailableBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        BigDecimal before = wallet.getAvailableBalance();
        BigDecimal after = before.subtract(amount);

        wallet.setAvailableBalance(after);

        walletRepository.save(wallet);

        WalletTransaction tx = new WalletTransaction(
                wallet.getId(),
                TransactionType.WITHDRAW,
                amount,
                null,
                before,
                after
        );

        transactionRepository.save(tx);

        return wallet;
    }
    
    //like ive mentioned in the transaction repo it SHOULD be faster when data gets big but idk for now
    @Transactional(readOnly = true)
    public Page<WalletTransaction> getTransactions(String user_id, Pageable pageable) {

        Wallet wallet = getWallet(user_id);

        return transactionRepository.findByWalletIdOrderByCreatedAtDesc(wallet.getId(), pageable);
    }

    @Transactional
    public Wallet holdBalance(String user_id, BigDecimal amount, String auct_id) {
        validateAmount(amount);

        if (transactionRepository.existsByAuctIdAndType(auct_id, TransactionType.HOLD)) {
            return getWallet(user_id);
        }

        Wallet wallet = getWallet(user_id);

        if (wallet.getAvailableBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        BigDecimal before = wallet.getAvailableBalance();
        BigDecimal after = before.subtract(amount);

        wallet.setAvailableBalance(after);
        wallet.setHeldBalance(wallet.getHeldBalance().add(amount));

        walletRepository.save(wallet);

        transactionRepository.save(new WalletTransaction(
                wallet.getId(),
                TransactionType.HOLD,
                amount,
                auct_id,
                before,
                after
        ));

        eventPublisher.publishEvent(
                new BalanceHeldEvent(user_id, amount, auct_id)
        );

        return wallet;
    }

    @Transactional
    public Wallet releaseBalance(String userId, BigDecimal amount, String auct_id) {
        validateAmount(amount);

        if (transactionRepository.existsByAuctIdAndType(auct_id, TransactionType.RELEASE)) {
            return getWallet(userId);
        }

        Wallet wallet = getWallet(userId);

        if (wallet.getHeldBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient held balance");
        }

        BigDecimal before = wallet.getAvailableBalance();

        wallet.setHeldBalance(wallet.getHeldBalance().subtract(amount));
        wallet.setAvailableBalance(wallet.getAvailableBalance().add(amount));

        BigDecimal after = wallet.getAvailableBalance();

        walletRepository.save(wallet);

        transactionRepository.save(new WalletTransaction(
                wallet.getId(),
                TransactionType.RELEASE,
                amount,
                auct_id,
                before,
                after
        ));

        eventPublisher.publishEvent(
                new BalanceReleasedEvent(userId, amount, auct_id)
        );

        return wallet;
    }

    @Transactional
    public Wallet convertToPayment(String user_id, BigDecimal amount, String auct_id) {

        validateAmount(amount);

        if (transactionRepository.existsByAuctIdAndType(auct_id, TransactionType.PAYMENT)) {
            return getWallet(user_id);
        }

        Wallet wallet = getWallet(user_id);

        if (wallet.getHeldBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient held balance");
        }

        BigDecimal before = wallet.getHeldBalance();

        wallet.setHeldBalance(wallet.getHeldBalance().subtract(amount));

        BigDecimal after = wallet.getHeldBalance();

        walletRepository.save(wallet);

        transactionRepository.save(new WalletTransaction(
                wallet.getId(),
                TransactionType.PAYMENT,
                amount,
                auct_id,
                before,
                after
        ));

        eventPublisher.publishEvent(
                new PaymentCompletedEvent(user_id, amount, auct_id)
        );

        return wallet;
    }

    private void validateAmount(BigDecimal amount) {
    if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
        throw new RuntimeException("Amount must be greater than zero");
    }
}


}

