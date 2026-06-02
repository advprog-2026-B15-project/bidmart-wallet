package model;

import com.example.wallet.model.Wallet;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class WalletTest {

    @Test
    void defaultBalances_ShouldBeZero() {
        Wallet wallet = new Wallet();
        assertEquals(BigDecimal.ZERO, wallet.getAvailableBalance());
        assertEquals(BigDecimal.ZERO, wallet.getHeldBalance());
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        Wallet wallet = new Wallet();
        wallet.setId("wallet-1");
        wallet.setUserId("user-1");
        wallet.setAvailableBalance(new BigDecimal("500"));
        wallet.setHeldBalance(new BigDecimal("100"));

        assertEquals("wallet-1", wallet.getId());
        assertEquals("user-1", wallet.getUserId());
        assertEquals(new BigDecimal("500"), wallet.getAvailableBalance());
        assertEquals(new BigDecimal("100"), wallet.getHeldBalance());
    }

    @Test
    void prePersist_ShouldSetCreatedAtAndUpdatedAt() {
        Wallet wallet = new Wallet();
        assertNull(wallet.getCreatedAt());
        assertNull(wallet.getUpdatedAt());

        wallet.prePersist();

        assertNotNull(wallet.getCreatedAt());
        assertNotNull(wallet.getUpdatedAt());
    }

    @Test
    void preUpdate_ShouldUpdateUpdatedAt() throws InterruptedException {
        Wallet wallet = new Wallet();
        wallet.prePersist();

        Thread.sleep(10);
        wallet.preUpdate();

        assertNotNull(wallet.getUpdatedAt());
        // updatedAt should be >= createdAt
        assertFalse(wallet.getUpdatedAt().isBefore(wallet.getCreatedAt()));
    }
}
