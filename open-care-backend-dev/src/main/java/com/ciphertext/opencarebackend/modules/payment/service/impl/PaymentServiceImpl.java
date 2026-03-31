package com.ciphertext.opencarebackend.modules.payment.service.impl;
import com.ciphertext.opencarebackend.modules.payment.dto.request.CreatePaymentOrderRequest;
import com.ciphertext.opencarebackend.modules.payment.dto.request.VerifyPaymentRequest;
import com.ciphertext.opencarebackend.modules.payment.dto.response.PaymentResponse;
import com.ciphertext.opencarebackend.entity.Payment;
import com.ciphertext.opencarebackend.entity.Profile;
import com.ciphertext.opencarebackend.enums.PaymentStatus;
import com.ciphertext.opencarebackend.mapper.PaymentMapper;
import com.ciphertext.opencarebackend.modules.payment.repository.PaymentRepository;
import com.ciphertext.opencarebackend.modules.user.repository.ProfileRepository;
import com.ciphertext.opencarebackend.modules.payment.service.PaymentService;
import com.ciphertext.opencarebackend.modules.payment.service.RazorpayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final ProfileRepository profileRepository;
    private final RazorpayService razorpayService;
    private final PaymentMapper paymentMapper;

    @Override
    public PaymentResponse createPaymentOrder(CreatePaymentOrderRequest request, String keycloakUserId) {
        log.info("Creating payment order for user: {}, entity: {}, amount: {}", keycloakUserId, request.entityType(), request.amount());

        // Fetch profile
        Profile profile = profileRepository.findByKeycloakUserId(keycloakUserId)
                .orElseThrow(() -> new RuntimeException("User profile not found"));

        // Generate receipt
        String receipt = Optional.ofNullable(request.receipt())
                .orElse("RCP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        try {
            // Create Razorpay order
            JSONObject razorpayOrder = razorpayService.createOrder(
                    request.amount(),
                    "INR",
                    receipt,
                    buildPaymentNotes(request)
            );

            // Save payment entity
            Payment payment = new Payment();
            payment.setRazorpayOrderId(razorpayOrder.getString("id"));
            payment.setAmount(request.amount());
            payment.setCurrency("INR");
            payment.setReceipt(receipt);
            payment.setStatus(PaymentStatus.PENDING);
            payment.setEntityType(request.entityType());
            payment.setEntityId(request.entityId());
            payment.setProfile(profile);
            payment.setRazorpayResponse(razorpayOrder.toString());
            payment.setNotes(request.notes());
            payment.setWebhookProcessed(false);

            Payment savedPayment = paymentRepository.save(payment);
            log.info("Payment order created successfully: orderId={}, paymentId={}", savedPayment.getRazorpayOrderId(), savedPayment.getId());

            return paymentMapper.toResponse(savedPayment);
        } catch (Exception e) {
            log.error("Failed to create payment order", e);
            throw new RuntimeException("Failed to create payment order: " + e.getMessage(), e);
        }
    }

    @Override
    public PaymentResponse verifyAndCapturePayment(VerifyPaymentRequest request, String keycloakUserId) {
        log.info("Verifying payment: orderId={}, paymentId={}, userId={}", request.orderId(), request.paymentId(), keycloakUserId);

        // Verify signature
        if (!razorpayService.verifyPaymentSignature(request.orderId(), request.paymentId(), request.signature())) {
            log.error("Payment signature verification failed");
            throw new RuntimeException("Payment verification failed: Invalid signature");
        }

        // Find payment by order ID
        Payment payment = paymentRepository.findByRazorpayOrderId(request.orderId())
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        // Verify user ownership
        if (!payment.getProfile().getKeycloakUserId().equals(keycloakUserId)) {
            log.error("Unauthorized payment verification attempt");
            throw new RuntimeException("Unauthorized: Payment does not belong to this user");
        }

        // Update payment details
        payment.setRazorpayPaymentId(request.paymentId());
        payment.setRazorpaySignature(request.signature());
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaidAt(LocalDateTime.now());

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment verified and captured: paymentId={}, status={}", savedPayment.getRazorpayPaymentId(), savedPayment.getStatus());

        return paymentMapper.toResponse(savedPayment);
    }

    @Override
    @Transactional
    public void handleWebhook(String payload, String signature) {
        log.info("Handling Razorpay webhook");

        // Verify webhook signature
        if (!razorpayService.verifyWebhookSignature(payload, signature)) {
            log.error("Webhook signature verification failed");
            throw new RuntimeException("Webhook verification failed: Invalid signature");
        }

        try {
            JSONObject event = new JSONObject(payload);
            String eventType = event.getString("event");
            JSONObject eventData = event.getJSONObject("payload").getJSONObject("payment").getJSONObject("entity");

            log.info("Processing webhook event: {}", eventType);

            switch (eventType) {
                case "payment.authorized":
                    handlePaymentAuthorized(eventData);
                    break;
                case "payment.captured":
                    handlePaymentCaptured(eventData);
                    break;
                case "payment.failed":
                    handlePaymentFailed(eventData);
                    break;
                case "refund.created":
                    handleRefundCreated(eventData);
                    break;
                default:
                    log.warn("Unknown webhook event type: {}", eventType);
            }
        } catch (Exception e) {
            log.error("Failed to process webhook", e);
            throw new RuntimeException("Failed to process webhook: " + e.getMessage(), e);
        }
    }

    @Override
    public PaymentResponse initiateRefund(Long paymentId, BigDecimal amount, String reason, String keycloakUserId) {
        log.info("Initiating refund for payment: {}, amount: {}, reason: {}", paymentId, amount, reason);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        // Verify user ownership
        if (!payment.getProfile().getKeycloakUserId().equals(keycloakUserId)) {
            log.error("Unauthorized refund attempt");
            throw new RuntimeException("Unauthorized: Payment does not belong to this user");
        }

        if (payment.getRazorpayPaymentId() == null) {
            log.error("Cannot refund: Payment ID not present");
            throw new RuntimeException("Cannot refund: Payment not completed");
        }

        try {
            // Call Razorpay refund API
            JSONObject refundResponse = razorpayService.refundPayment(
                    payment.getRazorpayPaymentId(),
                    amount,
                    reason
            );

            // Update payment
            payment.setRefundId(refundResponse.getString("id"));
            payment.setRefundAmount(amount);
            payment.setRefundStatus("PENDING");

            Payment savedPayment = paymentRepository.save(payment);
            log.info("Refund initiated successfully: refundId={}", savedPayment.getRefundId());

            return paymentMapper.toResponse(savedPayment);
        } catch (Exception e) {
            log.error("Failed to initiate refund", e);
            throw new RuntimeException("Failed to initiate refund: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getMyPayments(String keycloakUserId) {
        log.info("Fetching payments for user: {}", keycloakUserId);

        Profile profile = profileRepository.findByKeycloakUserId(keycloakUserId)
                .orElseThrow(() -> new RuntimeException("User profile not found"));

        Page<Payment> page = paymentRepository.findByProfileId(profile.getId(), PageRequest.of(0, 50));
        return page.getContent().stream()
                .map(paymentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long id, String keycloakUserId) {
        log.info("Fetching payment: id={}, userId={}", id, keycloakUserId);

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        // Verify user ownership
        if (!payment.getProfile().getKeycloakUserId().equals(keycloakUserId)) {
            log.error("Unauthorized payment access attempt");
            throw new RuntimeException("Unauthorized: Payment does not belong to this user");
        }

        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByOrderId(String orderId) {
        log.info("Fetching payment by order ID: {}", orderId);

        Payment payment = paymentRepository.findByRazorpayOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        return paymentMapper.toResponse(payment);
    }

    // ==================== Helper Methods ====================

    private void handlePaymentAuthorized(JSONObject eventData) {
        log.info("Handling payment.authorized event");
        String paymentId = eventData.getString("id");

        paymentRepository.findByRazorpayPaymentId(paymentId)
            .ifPresentOrElse(
                p -> {
                    p.setStatus(PaymentStatus.PENDING);
                    paymentRepository.save(p);
                    log.info("Payment authorized: {}", paymentId);
                },
                () -> log.warn("Payment not found for authorized event: {}", paymentId)
            );
    }

    private void handlePaymentCaptured(JSONObject eventData) {
        log.info("Handling payment.captured event");
        String paymentId = eventData.getString("id");

        paymentRepository.findByRazorpayPaymentId(paymentId)
            .ifPresentOrElse(
                p -> {
                    p.setStatus(PaymentStatus.PAID);
                    p.setWebhookProcessed(true);
                    p.setPaidAt(LocalDateTime.now());
                    paymentRepository.save(p);
                    log.info("Payment captured: {}", paymentId);
                },
                () -> log.warn("Payment not found for captured event: {}", paymentId)
            );
    }

    private void handlePaymentFailed(JSONObject eventData) {
        log.info("Handling payment.failed event");
        String paymentId = eventData.getString("id");

        paymentRepository.findByRazorpayPaymentId(paymentId)
                .ifPresentOrElse(
                        p -> {
                            p.setStatus(PaymentStatus.FAILED);
                            p.setWebhookProcessed(true);
                            if (eventData.has("error_code")) {
                                p.setErrorCode(eventData.getString("error_code"));
                            }
                            if (eventData.has("error_description")) {
                                p.setErrorDescription(eventData.getString("error_description"));
                            }
                            paymentRepository.save(p);
                            log.info("Payment failed: {}", paymentId);
                        },
                        () -> log.warn("Payment not found for failed event: {}", paymentId)
                );
    }

    private void handleRefundCreated(JSONObject eventData) {
        log.info("Handling refund.created event");
        String refundId = eventData.getString("id");
        String paymentId = eventData.getString("payment_id");

        paymentRepository.findByRazorpayPaymentId(paymentId)
            .ifPresentOrElse(
                p -> {
                    p.setRefundId(refundId);
                    p.setRefundStatus("INITIATED");
                    paymentRepository.save(p);
                    log.info("Refund created: refundId={}", refundId);
                },
                () -> log.warn("Payment not found for refund event: {}", paymentId)
            );
    }

    private java.util.Map<String, String> buildPaymentNotes(CreatePaymentOrderRequest request) {
        java.util.Map<String, String> notes = new java.util.HashMap<>();
        notes.put("entityType", request.entityType());
        notes.put("entityId", request.entityId().toString());
        if (request.notes() != null && !request.notes().isEmpty()) {
            notes.put("description", request.notes());
        }
        return notes;
    }
}
