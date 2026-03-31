package com.ciphertext.opencarebackend.modules.shared.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
    name = "payments",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_payments_bill_gateway_txn", columnNames = {"bill_id", "gateway_txn_id"})
    }
)
public class HmsPaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bill_id", nullable = false)
    private Long billId;

    @Column(name = "paid_by_user_id")
    private Long paidByUserId;

    @Column(name = "payment_reference", length = 100)
    private String paymentReference;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "payment_method", nullable = false, length = 30)
    private String paymentMethod;

    @Column(name = "payment_status", nullable = false, length = 20)
    private String paymentStatus = "SUCCESS";

    @Column(name = "paid_at", nullable = false)
    private LocalDateTime paidAt = LocalDateTime.now();

    @Column(name = "gateway", length = 50)
    private String gateway;

    @Column(name = "gateway_txn_id", length = 120)
    private String gatewayTxnId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
}