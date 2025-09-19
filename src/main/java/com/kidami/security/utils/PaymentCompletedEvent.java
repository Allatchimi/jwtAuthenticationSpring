package com.kidami.security.utils;

import com.kidami.security.models.Purchase;

// Event
public class PaymentCompletedEvent {

    private final Purchase purchase;
    public PaymentCompletedEvent(Purchase purchase) {
        this.purchase = purchase;
    }

    public Purchase getPurchase() {
        return purchase;
    }
}
