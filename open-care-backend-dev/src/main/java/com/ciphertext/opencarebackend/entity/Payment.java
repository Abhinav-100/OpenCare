package com.ciphertext.opencarebackend.entity;

import com.ciphertext.opencarebackend.enums.PaymentMethod;
import com.ciphertext.opencarebackend.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "payment", indexes = {
        @Index(name = "idx_payment_order_id", columnList = "razorpay_order_id"),
        @Index(name = "idx_payment_payment_id", columnList = "razorpay_payment_id"),
        @Index(name = "idx_payment_entity", columnList = "entity_type, entity_id")
})
public class Payment extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "razorpay_order_id", nullable = false, unique = true, length = 100)
    private String razorpayOrderId;

    @Column(name = "razorpay_payment_id", length = 100)
    private String razorpayPaymentId;

    @Column(name = "razorpay_signature", length = 500)
    private String razorpaySignature;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 10)
    private String currency = "INR";

    @Column(name = "receipt", length = 100)
    private String receipt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 20)
    private PaymentMethod paymentMethod;

    @Column(name = "entity_type", nullable = false, length = 50)
    private String entityType; // APPOINTMENT, LAB_TEST, AMBULANCE

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    @Column(name = "razorpay_response", columnDefinition = "TEXT")
    private String razorpayResponse;

    @Column(name = "error_code", length = 100)
    private String errorCode;

    @Column(name = "error_description", columnDefinition = "TEXT")
    private String errorDescription;

    @Column(name = "refund_id", length = 100)
    private String refundId;

    @Column(name = "refund_amount", precision = 10, scale = 2)
    private BigDecimal refundAmount;

    @Column(name = "refund_status", length = 50)
    private String refundStatus;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "webhook_processed", nullable = false)
    private Boolean webhookProcessed = false;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}