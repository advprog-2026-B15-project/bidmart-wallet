package com.example.wallet.DTO;

import java.math.BigDecimal;

public class HoldRequest {

    private String user_id;
    private BigDecimal amount;
    private String auct_id; // put id for auctions here

    public String getUserId() { return user_id; }
    public void setUserId(String user_id) { this.user_id = user_id; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getAuctId() { return auct_id; }
    public void setAuctId(String auct_id) { this.auct_id = auct_id; }
}