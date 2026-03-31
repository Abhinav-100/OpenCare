package com.ciphertext.opencarebackend.modules.payment.billing.service.impl;

import com.ciphertext.opencarebackend.exception.BadRequestException;
import com.ciphertext.opencarebackend.exception.ResourceNotFoundException;
import com.ciphertext.opencarebackend.modules.payment.billing.dto.BillResponse;
import com.ciphertext.opencarebackend.modules.payment.billing.dto.GenerateBillRequest;
import com.ciphertext.opencarebackend.modules.payment.billing.dto.PaymentTrackResponse;
import com.ciphertext.opencarebackend.modules.payment.billing.dto.RecordPaymentRequest;
import com.ciphertext.opencarebackend.modules.payment.billing.service.BillingModuleService;
import com.ciphertext.opencarebackend.modules.shared.entity.HmsAppointmentEntity;
import com.ciphertext.opencarebackend.modules.shared.entity.HmsBillEntity;
import com.ciphertext.opencarebackend.modules.shared.entity.HmsPatientEntity;
import com.ciphertext.opencarebackend.modules.shared.entity.HmsPaymentEntity;
import com.ciphertext.opencarebackend.modules.shared.dto.ModuleOverviewResponse;
import com.ciphertext.opencarebackend.modules.shared.repository.HmsAppointmentRepository;
import com.ciphertext.opencarebackend.modules.shared.repository.HmsBillRepository;
import com.ciphertext.opencarebackend.modules.shared.repository.HmsPatientRepository;
import com.ciphertext.opencarebackend.modules.shared.repository.HmsPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BillingModuleServiceImpl implements BillingModuleService {

    private static final Set<String> SUPPORTED_PAYMENT_METHODS = Set.of(
            "UPI", "CARD", "NET_BANKING", "WALLET", "CASH", "INSURANCE"
    );

    private final HmsBillRepository hmsBillRepository;
    private final HmsPaymentRepository hmsPaymentRepository;
    private final HmsAppointmentRepository hmsAppointmentRepository;
    private final HmsPatientRepository hmsPatientRepository;

    @Override
    @Transactional
    public BillResponse generateBill(GenerateBillRequest request) {
        hmsPatientRepository.findById(request.patientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + request.patientId()));

        if (request.dueDate() != null && request.dueDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Due date cannot be in the past");
        }

        if (request.appointmentId() != null) {
            HmsAppointmentEntity appointmentEntity = hmsAppointmentRepository.findById(request.appointmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Appointment not found: " + request.appointmentId()));
            if (!appointmentEntity.getPatientId().equals(request.patientId())) {
                throw new BadRequestException("Appointment does not belong to the provided patient");
            }
        }

        BigDecimal discount = request.discountAmount() == null ? BigDecimal.ZERO : request.discountAmount();
        BigDecimal tax = request.taxAmount() == null ? BigDecimal.ZERO : request.taxAmount();

        if (discount.compareTo(request.totalAmount()) > 0) {
            throw new BadRequestException("Discount amount cannot exceed total amount");
        }

        BigDecimal net = request.totalAmount().subtract(discount).add(tax);

        HmsBillEntity billEntity = new HmsBillEntity();
        billEntity.setBillNumber("BILL-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase());
        billEntity.setPatientId(request.patientId());
        billEntity.setAppointmentId(request.appointmentId());
        billEntity.setBillDate(LocalDate.now());
        billEntity.setTotalAmount(request.totalAmount());
        billEntity.setDiscountAmount(discount);
        billEntity.setTaxAmount(tax);
        billEntity.setNetAmount(net);
        billEntity.setStatus("UNPAID");
        billEntity.setDueDate(request.dueDate());
        billEntity.setNotes(request.notes());
        billEntity.setCreatedAt(LocalDateTime.now());
        billEntity.setUpdatedAt(LocalDateTime.now());
        HmsBillEntity saved = hmsBillRepository.save(billEntity);

        return toBillResponse(saved);
    }

    @Override
    @Transactional
    public PaymentTrackResponse recordPayment(RecordPaymentRequest request, String authenticatedUserId, List<String> roleCodes) {
        Long paidByUserId;
        try {
            paidByUserId = Long.parseLong(authenticatedUserId);
        } catch (NumberFormatException ex) {
            throw new BadRequestException("Invalid authenticated user context");
        }

        HmsBillEntity billEntity = hmsBillRepository.findByIdForUpdate(request.billId())
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found: " + request.billId()));

        if ("VOID".equalsIgnoreCase(billEntity.getStatus())) {
            throw new BadRequestException("Payments are not allowed for void bills");
        }

        if (hasRole(roleCodes, "PATIENT")) {
            Long patientId = resolvePatientIdByUserId(paidByUserId);
            if (!billEntity.getPatientId().equals(patientId)) {
                throw new BadRequestException("Patients can only pay their own bills");
            }
        }

        if (request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Payment amount must be greater than zero");
        }

        String normalizedPaymentMethod = normalizePaymentMethod(request.paymentMethod());

        String normalizedGatewayTxnId = normalizeGatewayTxnId(request.gatewayTxnId());
        if (normalizedGatewayTxnId != null
                && hmsPaymentRepository.existsByBillIdAndGatewayTxnId(billEntity.getId(), normalizedGatewayTxnId)) {
            throw new BadRequestException("Duplicate payment transaction detected for this bill");
        }

        BigDecimal alreadyPaid = hmsPaymentRepository.sumSuccessfulAmountByBillId(billEntity.getId());

        BigDecimal remainingDue = billEntity.getNetAmount().subtract(alreadyPaid);
        if (remainingDue.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Bill is already fully paid");
        }
        if (request.amount().compareTo(remainingDue) > 0) {
            throw new BadRequestException("Payment amount exceeds remaining due");
        }

        HmsPaymentEntity paymentEntity = new HmsPaymentEntity();
        paymentEntity.setBillId(billEntity.getId());
        paymentEntity.setPaidByUserId(paidByUserId);
        paymentEntity.setPaymentReference("PAY-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase());
        paymentEntity.setAmount(request.amount());
        paymentEntity.setPaymentMethod(normalizedPaymentMethod);
        paymentEntity.setPaymentStatus("SUCCESS");
        paymentEntity.setPaidAt(LocalDateTime.now());
        paymentEntity.setGateway(request.gateway());
        paymentEntity.setGatewayTxnId(normalizedGatewayTxnId);
        paymentEntity.setCreatedAt(LocalDateTime.now());
        paymentEntity.setUpdatedAt(LocalDateTime.now());
        HmsPaymentEntity savedPayment;
        try {
            savedPayment = hmsPaymentRepository.save(paymentEntity);
        } catch (DataIntegrityViolationException ex) {
            if (normalizedGatewayTxnId != null && !normalizedGatewayTxnId.isBlank()) {
                return hmsPaymentRepository.findFirstByBillIdAndGatewayTxnId(billEntity.getId(), normalizedGatewayTxnId)
                        .map(this::toPaymentResponse)
                        .orElseThrow(() -> new BadRequestException("Duplicate payment transaction detected for this bill"));
            }
            throw ex;
        }

        BigDecimal totalPaid = alreadyPaid.add(request.amount());

        if (totalPaid.compareTo(billEntity.getNetAmount()) >= 0) {
            billEntity.setStatus("PAID");
        } else if (totalPaid.compareTo(BigDecimal.ZERO) > 0) {
            billEntity.setStatus("PARTIAL");
        } else {
            billEntity.setStatus("UNPAID");
        }
        billEntity.setUpdatedAt(LocalDateTime.now());
        hmsBillRepository.save(billEntity);

        return toPaymentResponse(savedPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentTrackResponse> getPaymentsForBill(Long billId, String authenticatedUserId, List<String> roleCodes) {
        HmsBillEntity billEntity = hmsBillRepository.findById(billId)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found: " + billId));

        if (hasRole(roleCodes, "PATIENT")) {
            Long authenticatedUser;
            try {
                authenticatedUser = Long.parseLong(authenticatedUserId);
            } catch (NumberFormatException ex) {
                throw new BadRequestException("Invalid authenticated user context");
            }

            Long patientId = resolvePatientIdByUserId(authenticatedUser);
            if (!billEntity.getPatientId().equals(patientId)) {
                throw new BadRequestException("Patients can only view payments for their own bills");
            }
        }

        return hmsPaymentRepository.findByBillIdOrderByPaidAtDesc(billId)
                .stream()
                .map(this::toPaymentResponse)
                .toList();
    }

    @Override
    public ModuleOverviewResponse getOverview() {
        return new ModuleOverviewResponse(
                "billing",
                "active",
                List.of("bill-generation", "payment-tracking", "refund-processing")
        );
    }

    private BillResponse toBillResponse(HmsBillEntity billEntity) {
        return new BillResponse(
                billEntity.getId(),
                billEntity.getBillNumber(),
                billEntity.getPatientId(),
                billEntity.getAppointmentId(),
                billEntity.getBillDate(),
                billEntity.getTotalAmount(),
                billEntity.getDiscountAmount(),
                billEntity.getTaxAmount(),
                billEntity.getNetAmount(),
                billEntity.getStatus(),
                billEntity.getDueDate()
        );
    }

    private PaymentTrackResponse toPaymentResponse(HmsPaymentEntity paymentEntity) {
        return new PaymentTrackResponse(
                paymentEntity.getId(),
                paymentEntity.getBillId(),
                paymentEntity.getPaymentReference(),
                paymentEntity.getAmount(),
                paymentEntity.getPaymentMethod(),
                paymentEntity.getPaymentStatus(),
                paymentEntity.getPaidAt(),
                paymentEntity.getGateway(),
                paymentEntity.getGatewayTxnId()
        );
    }

    private boolean hasRole(List<String> roleCodes, String roleCode) {
        return roleCodes != null && roleCodes.stream().anyMatch(code -> roleCode.equalsIgnoreCase(code));
    }

    private Long resolvePatientIdByUserId(Long userId) {
        HmsPatientEntity patientEntity = hmsPatientRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found for authenticated user"));
        return patientEntity.getId();
    }

    private String normalizePaymentMethod(String paymentMethod) {
        String normalized = paymentMethod == null ? "" : paymentMethod.trim().toUpperCase(Locale.ROOT);
        if (!SUPPORTED_PAYMENT_METHODS.contains(normalized)) {
            throw new BadRequestException("Unsupported payment method: " + paymentMethod);
        }
        return normalized;
    }

    private String normalizeGatewayTxnId(String gatewayTxnId) {
        if (gatewayTxnId == null) {
            return null;
        }

        String normalized = gatewayTxnId.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}