package com.ciphertext.opencarebackend.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum PaymentMethod {
    UPI("UPI"),
    CARD("Card"),
    NETBANKING("Net Banking"),
    WALLET("Wallet"),
    EMI("EMI"),
    CASH("Cash");

    private final String description;

    PaymentMethod(String description) {
        this.description = description;
    }

    public String getValue() {
        return this.name();
    }

    public String getDescription() {
        return description;
    }
}