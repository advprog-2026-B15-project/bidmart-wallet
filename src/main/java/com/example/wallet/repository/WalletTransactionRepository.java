package com.example.wallet.repository;

import com.example.wallet.model.TransactionType;
import com.example.wallet.model.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, String> {

    List<WalletTransaction> findByWalletId(String walletId);

    //this one uses pagination so if theres too much id so its not slow 
    //(i think? it shouldnt be a problem for now but if data gets too big it would be nice)
    Page<WalletTransaction> findByWalletIdOrderByCreatedAtDesc(String walletId, Pageable pageable);

    boolean existsByAuctIdAndType(String auctId, TransactionType type);
}