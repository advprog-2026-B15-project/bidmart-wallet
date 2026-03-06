package com.example.wallet.repository;

import com.example.wallet.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface WalletRepository extends JpaRepository {

    Optional<Wallet> findByUserId(String userId);
}
