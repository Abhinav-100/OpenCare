package com.ciphertext.opencarebackend.modules.payment.dto.request;

import jakarta.validation.constraints.NotBlank;

public record VerifyPaymentRequest(
        @NotBlank(message = "Order ID is required")
        String orderId,

        @NotBlank(message = "Payment ID is required")
        String paymentId,

        @NotBlank(message = "Signature is required")
        String signature
) {
}