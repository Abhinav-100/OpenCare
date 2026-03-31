
export interface PaymentOrder {
  id: string;
  orderId: string;
  amount: number;
  currency: string;
  receipt: string;
  status: 'CREATED' | 'PAID' | 'FAILED' | 'EXPIRED';
  createdAt: string;
}

export interface Payment {
  id: number;
  razorpayOrderId: string;
  razorpayPaymentId?: string;
  razorpaySignature?: string;
  amount: number;
  currency: string;
  receipt?: string;
  status: 'PENDING' | 'PAID' | 'FAILED' | 'REFUNDED';
  paymentMethod?: 'UPI' | 'CARD' | 'NETBANKING' | 'WALLET' | 'EMI' | 'CASH';
  entityType: string;
  entityId: number;
  errorCode?: string;
  errorDescription?: string;
  refundId?: string;
  refundAmount?: number;
  refundStatus?: string;
  paidAt?: string;
  webhookProcessed: boolean;
  notes?: string;
  createdAt: string;
  updatedAt: string;
}

export interface PaymentVerification {
  razorpay_order_id: string;
  razorpay_payment_id: string;
  razorpay_signature: string;
}

export interface RazorpayOptions {
  key: string; // Razorpay key ID
  amount: number; // Amount in paise (multiply by 100)
  currency: string;
  name: string; // Business name
  description?: string;
  order_id: string; // Razorpay order ID
  handler: (response: PaymentResponse) => void;
  prefill?: {
    name?: string;
    email?: string;
    contact?: string;
  };
  notes?: Record<string, string>;
  theme?: {
    color?: string;
  };
}

export interface PaymentResponse {
  razorpay_payment_id: string;
  razorpay_order_id: string;
  razorpay_signature: string;
}

export interface RazorpayCheckoutModalProps {
  isOpen: boolean;
  onClose: () => void;
  orderDetails: PaymentOrder;
  onPaymentSuccess: (response: PaymentResponse) => void;
  onPaymentError?: (error: string) => void;
}

export interface PaymentStatus {
  value: 'PENDING' | 'PAID' | 'FAILED' | 'REFUNDED';
  label: string;
  color: 'yellow' | 'green' | 'red' | 'gray';
}
