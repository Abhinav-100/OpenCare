package com.ciphertext.opencarebackend.modules.payment.billing.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentTrackResponse(
        Long paymentId,
        Long billId,
        String paymentReference,
        BigDecimal amount,
        String paymentMethod,
        String paymentStatus,
        LocalDateTime paidAt,
        String gateway,
        String gatewayTxnId
) {
}