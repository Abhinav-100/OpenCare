package com.ciphertext.opencarebackend.modules.payment.billing.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BillResponse(
        Long billId,
        String billNumber,
        Long patientId,
        Long appointmentId,
        LocalDate billDate,
        BigDecimal totalAmount,
        BigDecimal discountAmount,
        BigDecimal taxAmount,
        BigDecimal netAmount,
        String status,
        LocalDate dueDate
) {
}