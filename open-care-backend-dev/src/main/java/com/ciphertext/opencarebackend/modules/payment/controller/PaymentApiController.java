package com.ciphertext.opencarebackend.modules.payment.controller;
import com.ciphertext.opencarebackend.modules.payment.dto.request.CreatePaymentOrderRequest;
import com.ciphertext.opencarebackend.modules.payment.dto.request.VerifyPaymentRequest;
import com.ciphertext.opencarebackend.modules.payment.dto.response.PaymentResponse;
import com.ciphertext.opencarebackend.modules.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Payments", description = "Payment management APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class PaymentApiController {

    private final PaymentService paymentService;

    /**
     * Create a payment order
     */
    @PostMapping("/create-order")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_DOCTOR', 'ROLE_ADMIN')")
    @Operation(summary = "Create a payment order", description = "Create a new Razorpay payment order for appointment/lab test booking")
    public ResponseEntity<PaymentResponse> createPaymentOrder(
            @Valid @RequestBody CreatePaymentOrderRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        log.info("Creating payment order: entityType={}, entityId={}, amount={}", request.entityType(), request.entityId(), request.amount());
        String keycloakUserId = jwt.getSubject();
        PaymentResponse response = paymentService.createPaymentOrder(request, keycloakUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Verify and capture payment
     */
    @PostMapping("/verify")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_DOCTOR', 'ROLE_ADMIN')")
    @Operation(summary = "Verify payment", description = "Verify payment signature and capture payment")
    public ResponseEntity<PaymentResponse> verifyPayment(
            @Valid @RequestBody VerifyPaymentRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        log.info("Verifying payment: orderId={}, paymentId={}", request.orderId(), request.paymentId());
        String keycloakUserId = jwt.getSubject();
        PaymentResponse response = paymentService.verifyAndCapturePayment(request, keycloakUserId);
        return ResponseEntity.ok(response);
    }

    /**
     * Razorpay webhook endpoint (no authentication required)
     */
    @PostMapping("/webhook")
    @Operation(summary = "Razorpay webhook", description = "Handle Razorpay webhook events")
    public ResponseEntity<Void> handleWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "X-Razorpay-Signature", required = false) String signature
    ) {
        log.info("Received Razorpay webhook");
        if (signature == null) {
            log.error("Missing Razorpay signature");
            return ResponseEntity.badRequest().build();
        }
        paymentService.handleWebhook(payload, signature);
        return ResponseEntity.ok().build();
    }

    /**
     * Get user's payment history
     */
    @GetMapping("/my")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_DOCTOR', 'ROLE_ADMIN')")
    @Operation(summary = "Get my payments", description = "Get current user's payment history")
    public ResponseEntity<List<PaymentResponse>> getMyPayments(
            @AuthenticationPrincipal Jwt jwt
    ) {
        log.info("Fetching payments for user: {}", jwt.getSubject());
        String keycloakUserId = jwt.getSubject();
        List<PaymentResponse> payments = paymentService.getMyPayments(keycloakUserId);
        return ResponseEntity.ok(payments);
    }

    /**
     * Get payment details
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_DOCTOR', 'ROLE_ADMIN')")
    @Operation(summary = "Get payment details", description = "Get payment details by ID")
    public ResponseEntity<PaymentResponse> getPaymentById(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        log.info("Fetching payment: id={}", id);
        String keycloakUserId = jwt.getSubject();
        PaymentResponse payment = paymentService.getPaymentById(id, keycloakUserId);
        return ResponseEntity.ok(payment);
    }

    /**
     * Initiate refund (admin/doctor only)
     */
    @PostMapping("/{id}/refund")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_DOCTOR')")
    @Operation(summary = "Initiate refund", description = "Initiate refund for a paid appointment")
    public ResponseEntity<PaymentResponse> initiateRefund(
            @PathVariable Long id,
            @RequestParam java.math.BigDecimal amount,
            @RequestParam(required = false) String reason,
            @AuthenticationPrincipal Jwt jwt
    ) {
        log.info("Initiating refund for payment: id={}, amount={}", id, amount);
        String keycloakUserId = jwt.getSubject();
        PaymentResponse response = paymentService.initiateRefund(id, amount, reason, keycloakUserId);
        return ResponseEntity.ok(response);
    }
}
