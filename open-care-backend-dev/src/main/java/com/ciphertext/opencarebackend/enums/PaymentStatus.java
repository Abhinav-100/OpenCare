package com.ciphertext.opencarebackend.enums;
import com.ciphertext.opencarebackend.modules.payment.dto.response.enums.PaymentStatusResponse;
import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum PaymentStatus {
    PENDING("Payment Pending"),
    PAID("Paid"),
    FAILED("Payment Failed"),
    REFUNDED("Refunded");

    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public PaymentStatusResponse toResponse() {
        return new PaymentStatusResponse(this.name(), description);
    }
}