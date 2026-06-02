package com.example.wallet.observer;


public interface WalletEventSubject {
    void registerObserver(WalletEventObserver observer);
    void removeObserver(WalletEventObserver observer);
    void notifyObservers(WalletEvent event);
}
