package com.example.wallet.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "wallet_transactions")
public class WalletTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "wallet_id", nullable = false)
    private String walletId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "auct_id", unique = true)
    private String auctId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public WalletTransaction(String walletId, TransactionType type, BigDecimal amount, String referenceId) {
        this.walletId = walletId;
        this.type = type;
        this.amount = amount;
        this.auctId = referenceId;
    }

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

}