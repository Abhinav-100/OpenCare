package com.ciphertext.opencarebackend.modules.patient.controller;

import com.ciphertext.opencarebackend.modules.auth.dto.ModuleAuthResponse;
import com.ciphertext.opencarebackend.modules.patient.dto.PatientAppointmentBookingRequest;
import com.ciphertext.opencarebackend.modules.patient.dto.PatientAppointmentResponse;
import com.ciphertext.opencarebackend.modules.patient.dto.PatientRegisterRequest;
import com.ciphertext.opencarebackend.modules.patient.dto.PatientReportResponse;
import com.ciphertext.opencarebackend.modules.patient.service.PatientModuleService;
import com.ciphertext.opencarebackend.modules.shared.dto.ModuleOverviewResponse;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/modules/patient")
@RequiredArgsConstructor
public class PatientModuleController {

    private final PatientModuleService patientModuleService;

    @Hidden
    @Deprecated(since = "2026-03", forRemoval = false)
    @PostMapping("/register")
    public ResponseEntity<ModuleAuthResponse> registerPatient(@Valid @RequestBody PatientRegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(patientModuleService.registerPatient(request));
    }

    @Hidden
    @Deprecated(since = "2026-03", forRemoval = false)
    @PostMapping("/appointments")
    public ResponseEntity<PatientAppointmentResponse> bookAppointment(
            @Valid @RequestBody PatientAppointmentBookingRequest request,
            Authentication authentication
    ) {
        PatientAppointmentResponse response = patientModuleService.bookAppointment(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/reports")
    public ResponseEntity<List<PatientReportResponse>> getMyReports(Authentication authentication) {
        return ResponseEntity.ok(patientModuleService.getMyReports(authentication.getName()));
    }

    @GetMapping("/overview")
    public ResponseEntity<ModuleOverviewResponse> getOverview() {
        return ResponseEntity.ok(patientModuleService.getOverview());
    }
}