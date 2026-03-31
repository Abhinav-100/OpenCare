package com.ciphertext.opencarebackend.modules.payment.billing.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GenerateBillRequest(
        @NotNull @Positive Long patientId,
        Long appointmentId,
        @NotNull @DecimalMin("0.01") BigDecimal totalAmount,
        @PositiveOrZero BigDecimal discountAmount,
        @PositiveOrZero BigDecimal taxAmount,
        LocalDate dueDate,
        String notes
) {
}