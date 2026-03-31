package com.ciphertext.opencarebackend.modules.payment.billing.service;

import com.ciphertext.opencarebackend.modules.payment.billing.dto.BillResponse;
import com.ciphertext.opencarebackend.modules.payment.billing.dto.GenerateBillRequest;
import com.ciphertext.opencarebackend.modules.payment.billing.dto.PaymentTrackResponse;
import com.ciphertext.opencarebackend.modules.payment.billing.dto.RecordPaymentRequest;
import com.ciphertext.opencarebackend.modules.shared.dto.ModuleOverviewResponse;

import java.util.List;

public interface BillingModuleService {

    BillResponse generateBill(GenerateBillRequest request);

    PaymentTrackResponse recordPayment(RecordPaymentRequest request, String authenticatedUserId, List<String> roleCodes);

    List<PaymentTrackResponse> getPaymentsForBill(Long billId, String authenticatedUserId, List<String> roleCodes);

    ModuleOverviewResponse getOverview();
}