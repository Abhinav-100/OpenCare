package com.ciphertext.opencarebackend.modules.payment.billing.controller;

import com.ciphertext.opencarebackend.modules.payment.billing.dto.BillResponse;
import com.ciphertext.opencarebackend.modules.payment.billing.dto.GenerateBillRequest;
import com.ciphertext.opencarebackend.modules.payment.billing.dto.PaymentTrackResponse;
import com.ciphertext.opencarebackend.modules.payment.billing.dto.RecordPaymentRequest;
import com.ciphertext.opencarebackend.modules.payment.billing.service.BillingModuleService;
import com.ciphertext.opencarebackend.modules.shared.dto.ModuleOverviewResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/modules/billing")
@RequiredArgsConstructor
public class BillingModuleController {

    private final BillingModuleService billingModuleService;

    @PostMapping("/bills/generate")
    public ResponseEntity<BillResponse> generateBill(@Valid @RequestBody GenerateBillRequest request) {
        return ResponseEntity.ok(billingModuleService.generateBill(request));
    }

    @PostMapping("/payments/record")
    public ResponseEntity<PaymentTrackResponse> recordPayment(
            @Valid @RequestBody RecordPaymentRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(billingModuleService.recordPayment(
            request,
            authentication.getName(),
            extractRoleCodes(authentication)
        ));
    }

    @GetMapping("/bills/{billId}/payments")
        public ResponseEntity<List<PaymentTrackResponse>> getPayments(
            @PathVariable Long billId,
            Authentication authentication
        ) {
        return ResponseEntity.ok(billingModuleService.getPaymentsForBill(
            billId,
            authentication.getName(),
            extractRoleCodes(authentication)
        ));
    }

    @GetMapping("/overview")
    public ResponseEntity<ModuleOverviewResponse> getOverview() {
        return ResponseEntity.ok(billingModuleService.getOverview());
    }

    @GetMapping("/admin/summary")
    public ResponseEntity<Map<String, Object>> getAdminSummary() {
        return ResponseEntity.ok(Map.of(
                "module", "billing",
                "scope", "admin",
                "message", "Protected billing summary endpoint"
        ));
    }

    private List<String> extractRoleCodes(Authentication authentication) {
        if (authentication == null || authentication.getAuthorities() == null) {
            return List.of();
        }

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(authority -> authority.startsWith("ROLE_") ? authority.substring(5) : authority)
                .toList();
    }
}