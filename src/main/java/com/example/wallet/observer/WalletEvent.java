package com.example.wallet.observer;

import java.math.BigDecimal;


public class WalletEvent {

    public enum Type {
        TOP_UP_COMPLETED,
        BALANCE_HELD,
        BALANCE_RELEASED,
        PAYMENT_COMPLETED
    }

    private final Type type;
    private final String userId;
    private final BigDecimal amount;
    private final String auctionId; // null if TOP_UP_COMPLETED

    public WalletEvent(Type type, String userId, BigDecimal amount, String auctionId) {
        this.type = type;
        this.userId = userId;
        this.amount = amount;
        this.auctionId = auctionId;
    }

    public Type getType() {
        return type;
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
