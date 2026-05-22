package model;

import org.junit.jupiter.api.Test;
import com.example.wallet.model.TransactionType;
import com.example.wallet.model.WalletTransaction;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class WalletTransactionTest {

    @Test
    void constructor_ShouldSetFieldsCorrectly() {
        WalletTransaction tx = new WalletTransaction(
                "wallet-1",
                TransactionType.HOLD,
                new BigDecimal("500"),
                "auction-1",
                "auction-1-user-1-hold",
                new BigDecimal("1000"),
                new BigDecimal("500")
        );

        assertEquals("wallet-1", tx.getWalletId());
        assertEquals(TransactionType.HOLD, tx.getType());
        assertEquals(new BigDecimal("500"), tx.getAmount());
        assertEquals("auction-1", tx.getAuctId());
        assertEquals("auction-1-user-1-hold", tx.getIdempotencyKey());
        assertEquals(new BigDecimal("1000"), tx.getBalanceBefore());
        assertEquals(new BigDecimal("500"), tx.getBalanceAfter());
    }

    @Test
    void prePersist_ShouldSetCreatedAt() {
        WalletTransaction tx = new WalletTransaction(
                "wallet-1",
                TransactionType.TOP_UP,
                new BigDecimal("100"),
                null,
                null,
                BigDecimal.ZERO,
                new BigDecimal("100")
        );

        assertNull(tx.getCreatedAt());

        tx.prePersist();

        assertNotNull(tx.getCreatedAt());
    }
}
