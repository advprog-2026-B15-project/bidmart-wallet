package com.example.wallet.listener;

import com.example.wallet.event.*;
import com.example.wallet.publisher.WalletRabbitPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WalletEventListener {

    private final WalletRabbitPublisher rabbitPublisher;

    @EventListener
    public void handleBalanceHeld(BalanceHeldEvent event) {
        // no RabbitMQ publish needed for hold — auction already knows
    }

    @EventListener
    public void handleBalanceReleased(BalanceReleasedEvent event) {
        rabbitPublisher.publishBalanceReleased(event.getUserId(), event.getAuctionId(), event.getAmount());
    }

    @EventListener
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        rabbitPublisher.publishBalanceConverted(event.getUserId(), event.getAuctionId(), event.getAmount());
    }

    @EventListener
    public void handleTopUpCompleted(TopUpCompletedEvent event) {
        // no downstream consumer needs this
    }
}
