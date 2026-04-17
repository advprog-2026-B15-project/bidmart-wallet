package com.example.wallet.repository;

import com.example.wallet.model.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, String> {

    List<WalletTransaction> findByWalletId(String walletId);
    boolean existsByReferenceId(String referenceId);
}
