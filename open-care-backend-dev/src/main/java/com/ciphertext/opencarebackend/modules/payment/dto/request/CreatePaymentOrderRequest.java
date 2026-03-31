package com.ciphertext.opencarebackend.modules.payment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreatePaymentOrderRequest(
        @NotBlank(message = "Entity type is required")
        String entityType,

        @NotNull(message = "Entity ID is required")
        Long entityId,

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be greater than 0")
        BigDecimal amount,

        String receipt,

        String notes
) {
}