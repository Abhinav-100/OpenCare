package com.ciphertext.opencarebackend.modules.doctor.controller;

import com.ciphertext.opencarebackend.modules.doctor.dto.DoctorAppointmentResponse;
import com.ciphertext.opencarebackend.modules.doctor.dto.DoctorDiagnosisRequest;
import com.ciphertext.opencarebackend.modules.doctor.dto.DoctorLabTestRequest;
import com.ciphertext.opencarebackend.modules.doctor.dto.DoctorLabTestResponse;
import com.ciphertext.opencarebackend.modules.doctor.service.DoctorModuleService;
import com.ciphertext.opencarebackend.modules.shared.dto.ModuleOverviewResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/modules/doctor")
@RequiredArgsConstructor
public class DoctorModuleController {

    private final DoctorModuleService doctorModuleService;

    @GetMapping("/appointments")
    public ResponseEntity<List<DoctorAppointmentResponse>> getMyAppointments(Authentication authentication) {
        return ResponseEntity.ok(doctorModuleService.getMyAppointments(authentication.getName()));
    }

    @PatchMapping("/appointments/{appointmentId}/diagnosis")
    public ResponseEntity<DoctorAppointmentResponse> addDiagnosis(
            @PathVariable Long appointmentId,
            @Valid @RequestBody DoctorDiagnosisRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(doctorModuleService.addDiagnosis(appointmentId, request, authentication.getName()));
    }

    @PostMapping("/lab-tests/request")
    public ResponseEntity<DoctorLabTestResponse> requestLabTest(
            @Valid @RequestBody DoctorLabTestRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(doctorModuleService.requestLabTest(request, authentication.getName()));
    }

    @GetMapping("/overview")
    public ResponseEntity<ModuleOverviewResponse> getOverview() {
        return ResponseEntity.ok(doctorModuleService.getOverview());
    }
}