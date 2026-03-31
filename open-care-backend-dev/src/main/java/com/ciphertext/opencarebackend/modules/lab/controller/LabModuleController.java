package com.ciphertext.opencarebackend.modules.lab.controller;

import com.ciphertext.opencarebackend.modules.lab.dto.LabReportResponse;
import com.ciphertext.opencarebackend.modules.lab.dto.LabReportUploadRequest;
import com.ciphertext.opencarebackend.modules.lab.service.LabModuleService;
import com.ciphertext.opencarebackend.modules.shared.dto.ModuleOverviewResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/modules/lab")
@RequiredArgsConstructor
public class LabModuleController {

    private final LabModuleService labModuleService;

    @PostMapping("/reports/upload")
    public ResponseEntity<LabReportResponse> uploadReport(
            @Valid @RequestBody LabReportUploadRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(labModuleService.uploadReport(request, authentication.getName()));
    }

    @GetMapping("/overview")
    public ResponseEntity<ModuleOverviewResponse> getOverview() {
        return ResponseEntity.ok(labModuleService.getOverview());
    }
}