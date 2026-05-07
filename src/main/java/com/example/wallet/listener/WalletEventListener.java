package com.example.wallet.listener;

import com.example.wallet.event.*;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class WalletEventListener {

    @EventListener
    public void handleBalanceHeld(BalanceHeldEvent event) {
        System.out.println("EVENT: BalanceHeld -> " + event.getAuctionId());
    }

    @EventListener
    public void handleBalanceReleased(BalanceReleasedEvent event) {
        System.out.println("EVENT: BalanceReleased -> " + event.getAuctionId());
    }

    @EventListener
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        System.out.println("EVENT: PaymentCompleted -> " + event.getAuctionId());
    }

    @EventListener
    public void handleTopUpCompleted(TopUpCompletedEvent event) {
        System.out.println("EVENT: TopUpCompleted -> " + event.getUserId());
    }
}