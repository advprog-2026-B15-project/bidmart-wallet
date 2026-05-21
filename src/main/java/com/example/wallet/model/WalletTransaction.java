package com.example.wallet.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(
        name = "wallet_transactions",
        indexes = {
                @Index(name = "idx_wallet_tx_wallet_id", columnList = "wallet_id"),
                @Index(name = "idx_wallet_tx_auct_id", columnList = "auct_id"),
                @Index(name = "idx_wallet_tx_created_at", columnList = "created_at"),
                @Index(name = "idx_wallet_tx_type", columnList = "type")
        }
)
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


    @Column(name = "balance_before", nullable = false)
    private BigDecimal balanceBefore;

    @Column(name = "balance_after", nullable = false)
    private BigDecimal balanceAfter;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public WalletTransaction() {}

    public WalletTransaction(
            String walletId,
            TransactionType type,
            BigDecimal amount,
            String referenceId,
            BigDecimal balanceBefore,
            BigDecimal balanceAfter
    ) {
        this.walletId = walletId;
        this.type = type;
        this.amount = amount;
        this.auctId = referenceId;
        this.balanceBefore = balanceBefore;
        this.balanceAfter = balanceAfter;
    }

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}