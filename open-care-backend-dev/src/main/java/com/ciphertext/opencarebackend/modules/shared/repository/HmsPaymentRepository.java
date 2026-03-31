package com.ciphertext.opencarebackend.modules.shared.repository;

import com.ciphertext.opencarebackend.modules.shared.entity.HmsPaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface HmsPaymentRepository extends JpaRepository<HmsPaymentEntity, Long> {

    List<HmsPaymentEntity> findByBillIdOrderByPaidAtDesc(Long billId);

    @Query("select coalesce(sum(p.amount), 0) from HmsPaymentEntity p where p.billId = :billId and p.paymentStatus = 'SUCCESS'")
    BigDecimal sumSuccessfulAmountByBillId(@Param("billId") Long billId);

    boolean existsByBillIdAndGatewayTxnId(Long billId, String gatewayTxnId);

    Optional<HmsPaymentEntity> findFirstByBillIdAndGatewayTxnId(Long billId, String gatewayTxnId);
}