package com.example.wallet.event;

import java.math.BigDecimal;

public class TopUpCompletedEvent {

    private final String userId;
    private final BigDecimal amount;

    public TopUpCompletedEvent(String userId,
                               BigDecimal amount) {
        this.userId = userId;
        this.amount = amount;
    }

    public String getUserId() {
        return userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}