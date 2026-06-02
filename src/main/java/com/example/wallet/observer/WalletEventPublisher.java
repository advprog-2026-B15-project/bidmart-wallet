package com.example.wallet.observer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


@Slf4j
@Component
public class WalletEventPublisher implements WalletEventSubject {

    private final List<WalletEventObserver> observers = new CopyOnWriteArrayList<>();

    @Override
    public void registerObserver(WalletEventObserver observer) {
        observers.add(observer);
        log.info("Registered wallet observer: {}", observer.getClass().getSimpleName());
    }

    @Override
    public void removeObserver(WalletEventObserver observer) {
        observers.remove(observer);
        log.info("Removed wallet observer: {}", observer.getClass().getSimpleName());
    }

    @Override
    public void notifyObservers(WalletEvent event) {
        log.debug("Notifying {} observer(s) of event: {}", observers.size(), event.getType());
        for (WalletEventObserver observer : observers) {
            try {
                observer.onEvent(event);
            } catch (Exception e) {
                log.error("Observer {} failed handling event {}: {}",
                        observer.getClass().getSimpleName(), event.getType(), e.getMessage());
            }
        }
    }
}
