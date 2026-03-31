package com.ciphertext.opencarebackend.modules.payment.repository;

import com.ciphertext.opencarebackend.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);

    Optional<Payment> findByRazorpayPaymentId(String razorpayPaymentId);

    Page<Payment> findByProfileId(Long profileId, Pageable pageable);

    Page<Payment> findByProfileIdAndEntityType(Long profileId, String entityType, Pageable pageable);
}