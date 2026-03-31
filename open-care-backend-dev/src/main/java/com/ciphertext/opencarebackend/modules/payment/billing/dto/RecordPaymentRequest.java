package com.ciphertext.opencarebackend.modules.payment.billing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record RecordPaymentRequest(
        @NotNull @Positive Long billId,
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        @NotBlank String paymentMethod,
        String gateway,
        String gatewayTxnId
) {
}