package com.ciphertext.opencarebackend.modules.payment.dto.response;

import com.ciphertext.opencarebackend.enums.PaymentMethod;
import com.ciphertext.opencarebackend.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {
    private Long id;
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;
    private BigDecimal amount;
    private String currency;
    private String receipt;
    private PaymentStatus status;
    private PaymentMethod paymentMethod;
    private String entityType;
    private Long entityId;
    private String errorCode;
    private String errorDescription;
    private String refundId;
    private BigDecimal refundAmount;
    private String refundStatus;
    private LocalDateTime paidAt;
    private Boolean webhookProcessed;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}