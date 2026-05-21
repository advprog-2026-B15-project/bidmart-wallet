package com.example.wallet.event;

import java.math.BigDecimal;

public class BalanceHeldEvent {

    private final String userId;
    private final BigDecimal amount;
    private final String auctionId;

    public BalanceHeldEvent(String userId,
                            BigDecimal amount,
                            String auctionId) {
        this.userId = userId;
        this.amount = amount;
        this.auctionId = auctionId;
    }

    public String getUserId() {
        return userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getAuctionId() {
        return auctionId;
    }
}