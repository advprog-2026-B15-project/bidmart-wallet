package com.example.wallet.listener;

import com.example.wallet.observer.WalletEvent;
import com.example.wallet.observer.WalletEventObserver;
import com.example.wallet.observer.WalletEventPublisher;
import com.example.wallet.publisher.WalletRabbitPublisher;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class WalletEventListener implements WalletEventObserver {

    private final WalletRabbitPublisher rabbitPublisher;
    private final WalletEventPublisher walletEventPublisher;

    @PostConstruct
    public void register() {
        walletEventPublisher.registerObserver(this);
    }

    @Override
    public void onEvent(WalletEvent event) {
        switch (event.getType()) {
            case BALANCE_RELEASED -> handleBalanceReleased(event);
            case PAYMENT_COMPLETED -> handlePaymentCompleted(event);
            case BALANCE_HELD -> log.debug("BALANCE_HELD event received — no downstream publish needed");
            case TOP_UP_COMPLETED -> log.debug("TOP_UP_COMPLETED event received — no downstream publish needed");
            default -> log.warn("Unhandled wallet event type: {}", event.getType());
        }
    }

    private void handleBalanceReleased(WalletEvent event) {
        rabbitPublisher.publishBalanceReleased(event.getUserId(), event.getAuctionId(), event.getAmount());
    }

    private void handlePaymentCompleted(WalletEvent event) {
        rabbitPublisher.publishBalanceConverted(event.getUserId(), event.getAuctionId(), event.getAmount());
    }
}
