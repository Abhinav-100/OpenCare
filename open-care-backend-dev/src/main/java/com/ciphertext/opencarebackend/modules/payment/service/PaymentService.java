package com.ciphertext.opencarebackend.modules.payment.service;
import com.ciphertext.opencarebackend.modules.payment.dto.request.CreatePaymentOrderRequest;
import com.ciphertext.opencarebackend.modules.payment.dto.request.VerifyPaymentRequest;
import com.ciphertext.opencarebackend.modules.payment.dto.response.PaymentResponse;
import java.util.List;

public interface PaymentService {

    /**
     * Create a payment order
     */
    PaymentResponse createPaymentOrder(CreatePaymentOrderRequest request, String keycloakUserId);

    /**
     * Verify and capture payment
     */
    PaymentResponse verifyAndCapturePayment(VerifyPaymentRequest request, String keycloakUserId);

    /**
     * Handle Razorpay webhook
     */
    void handleWebhook(String payload, String signature);

    /**
     * Initiate refund
     */
    PaymentResponse initiateRefund(Long paymentId, java.math.BigDecimal amount, String reason, String keycloakUserId);

    /**
     * Get user's payment history
     */
    List<PaymentResponse> getMyPayments(String keycloakUserId);

    /**
     * Get payment details by ID
     */
    PaymentResponse getPaymentById(Long id, String keycloakUserId);

    /**
     * Get payment by order ID
     */
    PaymentResponse getPaymentByOrderId(String orderId);
}
