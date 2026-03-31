import { Payment, PaymentVerification } from "@/shared/types/payments";
import { apiGet, apiPost } from "@/shared/utils/api-client";

/**
 * Create a payment order
 */
export async function createPaymentOrder(
  entityType: string,
  entityId: number,
  amount: number,
  notes?: string
): Promise<Payment> {
  const response = await apiPost<Payment>("/payments/create-order", {
    entityType,
    entityId,
    amount,
    notes,
  });

  if (!response.ok || !response.data) {
    throw new Error(response.error || "Failed to create payment order");
  }

  return response.data;
}

/**
 * Verify payment signature and capture payment
 */
export async function verifyPayment(verification: PaymentVerification): Promise<Payment> {
  const response = await apiPost<Payment>("/payments/verify", {
    orderId: verification.razorpay_order_id,
    paymentId: verification.razorpay_payment_id,
    signature: verification.razorpay_signature,
  });

  if (!response.ok || !response.data) {
    throw new Error(response.error || "Failed to verify payment");
  }

  return response.data;
}

/**
 * Get user's payment history
 */
export async function getMyPayments(): Promise<Payment[]> {
  const response = await apiGet<Payment[]>("/payments/my", { includeAuth: true });

  if (!response.ok || !response.data) {
    throw new Error(response.error || "Failed to fetch payment history");
  }

  return response.data;
}

/**
 * Get payment details by ID
 */
export async function getPaymentById(id: number): Promise<Payment> {
  const response = await apiGet<Payment>(`/payments/${id}`, { includeAuth: true });

  if (!response.ok || !response.data) {
    throw new Error(response.error || "Failed to fetch payment details");
  }

  return response.data;
}

/**
 * Get payment by order ID
 */
export async function getPaymentByOrderId(orderId: string): Promise<Payment> {
  const payments = await getMyPayments();
  const payment = payments.find((item) => item.razorpayOrderId === orderId);

  if (!payment) {
    throw new Error("Payment not found for the provided order id");
  }

  return payment;
}

/**
 * Initiate refund
 */
export async function initiateRefund(
  paymentId: number,
  amount: number,
  reason?: string
): Promise<Payment> {
  const params = new URLSearchParams();
  params.append("amount", amount.toString());
  if (reason) {
    params.append("reason", reason);
  }

  const response = await apiPost<Payment>(
    `/payments/${paymentId}/refund?${params.toString()}`,
    {}
  );

  if (!response.ok || !response.data) {
    throw new Error(response.error || "Failed to initiate refund");
  }

  return response.data;
}
