'use client';

import React, { useEffect, useState } from 'react';
import { Loader2, AlertCircle, CheckCircle } from 'lucide-react';
import { toast } from 'sonner';
import { Button } from '@/modules/platform/components/ui/button';
import { createPaymentOrder, verifyPayment } from '@/modules/payments/api/payments';
import { Payment, RazorpayCheckoutModalProps, PaymentResponse } from '@/shared/types/payments';

interface RazorpayPaymentFailedEvent {
  error: {
    description: string;
  };
}

interface RazorpayCheckoutInstance {
  on: (
    event: 'payment.failed',
    handler: (response: RazorpayPaymentFailedEvent) => void
  ) => void;
  open: () => void;
}

interface RazorpayOptions {
  key: string;
  amount: number;
  currency: string;
  order_id: string;
  name: string;
  description: string;
  prefill: {
    name: string;
    email: string;
    contact: string;
  };
  theme: {
    color: string;
  };
  handler: (response: PaymentResponse) => Promise<void>;
  modal: {
    ondismiss: () => void;
  };
}

interface RazorpayConstructor {
  new (options: RazorpayOptions): RazorpayCheckoutInstance;
}

interface RazorpayWindow extends Window {
  Razorpay?: RazorpayConstructor;
}

const getErrorMessage = (error: unknown, fallback: string) => {
  if (error instanceof Error && error.message.trim() !== '') {
    return error.message;
  }
  return fallback;
};

/**
 * Razorpay Checkout Component
 * Handles payment flow: Create Order -> Open Checkout -> Verify Payment
 */
export function RazorpayCheckout({
  isOpen,
  onClose,
  orderDetails,
  onPaymentSuccess,
  onPaymentError,
}: RazorpayCheckoutModalProps) {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [paymentStatus, setPaymentStatus] = useState<'idle' | 'processing' | 'success' | 'error'>('idle');

  // Load Razorpay script on component mount
  useEffect(() => {
    if (!isOpen) return;

    const script = document.createElement('script');
    script.src = 'https://checkout.razorpay.com/v1/checkout.js';
    script.async = true;
    script.onload = () => {
      // Script loaded successfully
    };
    script.onerror = () => {
      setError('Failed to load Razorpay checkout. Please refresh the page.');
    };
    document.body.appendChild(script);

    return () => {
      if (script.parentNode) {
        document.body.removeChild(script);
      }
    };
  }, [isOpen]);

  /**
   * Initialize and open Razorpay checkout
   */
  const handleOpenCheckout = async () => {
    setLoading(true);
    setError(null);
    setPaymentStatus('processing');

    try {
      // Get Razorpay key from environment
      const razorpayKey = process.env.NEXT_PUBLIC_RAZORPAY_KEY_ID;
      if (!razorpayKey) {
        throw new Error('Razorpay configuration missing');
      }

      // Razorpay window type
      const razorpayWindow = window as RazorpayWindow;

      if (!razorpayWindow.Razorpay) {
        throw new Error('Razorpay script not loaded');
      }

      // Create Razorpay options
      const options = {
        key: razorpayKey,
        amount: Math.round(orderDetails.amount * 100), // Convert to paise
        currency: orderDetails.currency,
        order_id: orderDetails.orderId,
        name: 'OpenCare',
        description: `Payment for ${orderDetails.orderId}`,
        prefill: {
          name: '',
          email: '',
          contact: '',
        },
        theme: {
          color: '#0d9488', // Teal color matching OpenCare theme
        },
        handler: async (response: PaymentResponse) => {
          try {
            setPaymentStatus('processing');
            // Verify payment signature
            const verifiedPayment = await verifyPayment(response);

            if (verifiedPayment.status === 'PAID') {
              setPaymentStatus('success');
              toast.success('Payment successful!');
              onPaymentSuccess(response);

              // Close modal after success
              setTimeout(() => {
                onClose();
              }, 2000);
            } else {
              throw new Error('Payment verification failed');
            }
          } catch (err: unknown) {
            setPaymentStatus('error');
            const errorMsg = getErrorMessage(err, 'Payment verification failed');
            setError(errorMsg);
            toast.error(errorMsg);
            onPaymentError?.(errorMsg);
          }
        },
        modal: {
          ondismiss: () => {
            setPaymentStatus('idle');
            setLoading(false);
            toast.info('Payment cancelled');
          },
        },
      };

      // Open Razorpay checkout
      const razorpayCheckout = new razorpayWindow.Razorpay(options);
      razorpayCheckout.on('payment.failed', (response: RazorpayPaymentFailedEvent) => {
        setPaymentStatus('error');
        const errorMsg = `Payment failed: ${response.error.description}`;
        setError(errorMsg);
        toast.error(errorMsg);
        onPaymentError?.(errorMsg);
      });

      razorpayCheckout.open();
    } catch (err: unknown) {
      setPaymentStatus('error');
      const errorMsg = getErrorMessage(err, 'Failed to open payment checkout');
      setError(errorMsg);
      toast.error(errorMsg);
      onPaymentError?.(errorMsg);
    } finally {
      setLoading(false);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-lg shadow-lg max-w-sm w-full p-6">
        {/* Header */}
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-xl font-semibold text-gray-900">Complete Payment</h2>
          {!loading && paymentStatus !== 'processing' && (
            <button
              onClick={onClose}
              className="text-gray-400 hover:text-gray-600"
            >
              ✕
            </button>
          )}
        </div>

        {/* Payment Details */}
        {paymentStatus === 'idle' && (
          <>
            <div className="bg-gray-50 rounded-lg p-4 mb-6">
              <div className="flex justify-between items-center mb-2">
                <span className="text-gray-600">Amount</span>
                <span className="text-2xl font-bold text-teal-600">
                  ₹{orderDetails.amount.toFixed(2)}
                </span>
              </div>
              <div className="text-sm text-gray-500">
                Order ID: {orderDetails.orderId}
              </div>
            </div>

            {error && (
              <div className="bg-red-50 border border-red-200 rounded-lg p-3 mb-4 flex gap-3">
                <AlertCircle className="h-5 w-5 text-red-600 flex-shrink-0" />
                <p className="text-sm text-red-700">{error}</p>
              </div>
            )}

            {/* Action Buttons */}
            <div className="flex gap-3">
              <Button
                variant="outline"
                onClick={onClose}
                disabled={loading}
                className="flex-1"
              >
                Cancel
              </Button>
              <Button
                onClick={handleOpenCheckout}
                disabled={loading}
                className="flex-1 bg-teal-600 hover:bg-teal-700 text-white"
              >
                {loading ? (
                  <>
                    <Loader2 className="h-4 w-4 mr-2 animate-spin" />
                    Processing...
                  </>
                ) : (
                  'Pay with Razorpay'
                )}
              </Button>
            </div>

            <p className="text-xs text-gray-500 text-center mt-4">
              Secured by Razorpay Payment Gateway
            </p>
          </>
        )}

        {/* Processing State */}
        {paymentStatus === 'processing' && (
          <div className="flex flex-col items-center gap-4 py-8">
            <Loader2 className="h-12 w-12 text-teal-600 animate-spin" />
            <p className="text-gray-600">Processing your payment...</p>
          </div>
        )}

        {/* Success State */}
        {paymentStatus === 'success' && (
          <div className="flex flex-col items-center gap-4 py-8">
            <CheckCircle className="h-12 w-12 text-green-600" />
            <p className="text-gray-900 font-semibold">Payment Successful!</p>
            <p className="text-sm text-gray-600 text-center">
              Your booking has been confirmed. You will be redirected shortly.
            </p>
          </div>
        )}

        {/* Error State */}
        {paymentStatus === 'error' && (
          <div className="flex flex-col items-center gap-4 py-8">
            <AlertCircle className="h-12 w-12 text-red-600" />
            <p className="text-gray-900 font-semibold">Payment Failed</p>
            <p className="text-sm text-gray-600 text-center">{error}</p>
            <Button
              onClick={() => {
                setPaymentStatus('idle');
                setError(null);
              }}
              className="mt-4 bg-teal-600 hover:bg-teal-700 text-white"
            >
              Try Again
            </Button>
          </div>
        )}
      </div>
    </div>
  );
}

/**
 * Standalone checkout button
 * Use this if you don't want the modal wrapper
 */
export function RazorpayCheckoutButton({
  entityType,
  entityId,
  amount,
  notes,
  onSuccess,
  onError,
  className = '',
}: {
  entityType: string;
  entityId: number;
  amount: number;
  notes?: string;
  onSuccess: (payment: Payment) => void;
  onError?: (error: string) => void;
  className?: string;
}) {
  const [loading, setLoading] = useState(false);
  const [paymentOrder, setPaymentOrder] = useState<Payment | null>(null);
  const [showCheckout, setShowCheckout] = useState(false);

  const handleInitiatePayment = async () => {
    setLoading(true);
    try {
      const order = await createPaymentOrder(entityType, entityId, amount, notes);
      setPaymentOrder(order);
      setShowCheckout(true);
    } catch (err: unknown) {
      const errorMsg = getErrorMessage(err, 'Failed to create payment order');
      toast.error(errorMsg);
      onError?.(errorMsg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <Button
        onClick={handleInitiatePayment}
        disabled={loading}
        className={className}
      >
        {loading ? (
          <>
            <Loader2 className="h-4 w-4 mr-2 animate-spin" />
            Creating Order...
          </>
        ) : (
          `Pay ₹${amount.toFixed(2)}`
        )}
      </Button>

      {paymentOrder && showCheckout && (
        <RazorpayCheckout
          isOpen={showCheckout}
          onClose={() => {
            setShowCheckout(false);
            setPaymentOrder(null);
          }}
          orderDetails={{
            id: paymentOrder.id.toString(),
            orderId: paymentOrder.razorpayOrderId,
            amount: paymentOrder.amount,
            currency: paymentOrder.currency,
            receipt: paymentOrder.receipt || '',
            status: 'CREATED',
            createdAt: paymentOrder.createdAt,
          }}
          onPaymentSuccess={() => {
            setShowCheckout(false);
            setPaymentOrder(null);
            // Re-fetch payment details to confirm
            onSuccess(paymentOrder);
          }}
          onPaymentError={onError}
        />
      )}
    </>
  );
}
