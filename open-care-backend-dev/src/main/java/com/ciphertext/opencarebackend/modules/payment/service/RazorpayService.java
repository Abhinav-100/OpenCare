package com.ciphertext.opencarebackend.modules.payment.service;

import com.razorpay.Order;
import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import com.razorpay.Refund;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class RazorpayService {

    private static final Logger logger = LoggerFactory.getLogger(RazorpayService.class);

    @Value("${razorpay.key-id:}")
    private String razorpayKeyId;

    @Value("${razorpay.key-secret:}")
    private String razorpayKeySecret;

    @Value("${razorpay.webhook-secret:}")
    private String razorpayWebhookSecret;

    private RazorpayClient razorpayClient;

    /**
     * Initialize Razorpay client (lazy initialization)
     */
    private RazorpayClient getRazorpayClient() {
        if (razorpayClient == null) {
            try {
                if (razorpayKeyId == null || razorpayKeyId.isBlank() ||
                        razorpayKeySecret == null || razorpayKeySecret.isBlank()) {
                    throw new IllegalStateException("Razorpay credentials are not configured");
                }
                razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
            } catch (Exception e) {
                logger.error("Failed to initialize Razorpay client", e);
                throw new RuntimeException("Razorpay initialization failed", e);
            }
        }
        return razorpayClient;
    }

    /**
     * Create a Razorpay order
     */
    public JSONObject createOrder(BigDecimal amount, String currency, String receipt, Map<String, String> notes) {
        try {
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amount.multiply(BigDecimal.valueOf(100)).longValue()); // Convert to paise
            orderRequest.put("currency", currency);
            orderRequest.put("receipt", receipt);

            if (notes != null && !notes.isEmpty()) {
                orderRequest.put("notes", notes);
            }

            logger.info("Creating Razorpay order: receipt={}, amount={}", receipt, amount);
            Order order = getRazorpayClient().orders.create(orderRequest);
            logger.info("Order created successfully: orderId={}", order.toJson().optString("id"));

            return order.toJson();
        } catch (Exception e) {
            logger.error("Failed to create Razorpay order", e);
            throw new RuntimeException("Failed to create payment order: " + e.getMessage(), e);
        }
    }

    /**
     * Verify payment signature
     */
    public boolean verifyPaymentSignature(String orderId, String paymentId, String signature) {
        try {
            JSONObject attributes = new JSONObject();
            attributes.put("razorpay_order_id", orderId);
            attributes.put("razorpay_payment_id", paymentId);
            attributes.put("razorpay_signature", signature);

            logger.info("Verifying payment signature for orderId={}, paymentId={}", orderId, paymentId);
            boolean isValid = Utils.verifyPaymentSignature(attributes, razorpayKeySecret);
            logger.info("Signature verification result: {}", isValid);

            return isValid;
        } catch (Exception e) {
            logger.error("Failed to verify payment signature", e);
            return false;
        }
    }

    /**
     * Capture payment
     */
    public JSONObject capturePayment(String paymentId, BigDecimal amount) {
        try {
            JSONObject captureRequest = new JSONObject();
            captureRequest.put("amount", amount.multiply(BigDecimal.valueOf(100)).longValue()); // Convert to paise

            logger.info("Capturing payment: paymentId={}, amount={}", paymentId, amount);
            Payment payment = getRazorpayClient().payments.capture(paymentId, captureRequest);
            logger.info("Payment captured successfully: status={}", payment.toJson().optString("status"));

            return payment.toJson();
        } catch (Exception e) {
            logger.error("Failed to capture payment", e);
            throw new RuntimeException("Failed to capture payment: " + e.getMessage(), e);
        }
    }

    /**
     * Fetch payment details
     */
    public JSONObject fetchPaymentDetails(String paymentId) {
        try {
            logger.info("Fetching payment details for paymentId={}", paymentId);
            Payment payment = getRazorpayClient().payments.fetch(paymentId);
            return payment.toJson();
        } catch (Exception e) {
            logger.error("Failed to fetch payment details", e);
            throw new RuntimeException("Failed to fetch payment details: " + e.getMessage(), e);
        }
    }

    /**
     * Refund payment
     */
    public JSONObject refundPayment(String paymentId, BigDecimal amount, String notes) {
        try {
            JSONObject refundRequest = new JSONObject();
            refundRequest.put("amount", amount.multiply(BigDecimal.valueOf(100)).longValue()); // Convert to paise
            if (notes != null && !notes.isEmpty()) {
                refundRequest.put("notes", notes);
            }

            logger.info("Refunding payment: paymentId={}, amount={}", paymentId, amount);
            Refund refund = getRazorpayClient().payments.refund(paymentId, refundRequest);
            logger.info("Refund initiated successfully: refundId={}", refund.toJson().optString("id"));

            return refund.toJson();
        } catch (Exception e) {
            logger.error("Failed to refund payment", e);
            throw new RuntimeException("Failed to refund payment: " + e.getMessage(), e);
        }
    }

    /**
     * Verify webhook signature
     */
    public boolean verifyWebhookSignature(String payload, String signature) {
        if (razorpayWebhookSecret == null || razorpayWebhookSecret.isEmpty()) {
            logger.warn("Webhook secret not configured");
            return false;
        }

        try {
            logger.info("Verifying webhook signature");
            boolean isValid = Utils.verifyWebhookSignature(payload, signature, razorpayWebhookSecret);
            logger.info("Webhook signature verification result: {}", isValid);
            return isValid;
        } catch (Exception e) {
            logger.error("Failed to verify webhook signature", e);
            return false;
        }
    }
}
