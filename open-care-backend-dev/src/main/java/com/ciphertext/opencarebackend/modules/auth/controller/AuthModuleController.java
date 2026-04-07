package com.ciphertext.opencarebackend.modules.auth.controller;

import com.ciphertext.opencarebackend.modules.auth.dto.ModuleAuthResponse;
import com.ciphertext.opencarebackend.modules.auth.dto.ModuleLoginRequest;
import com.ciphertext.opencarebackend.modules.auth.dto.ModuleRegisterRequest;
import com.ciphertext.opencarebackend.modules.auth.service.AuthModuleService;
import com.ciphertext.opencarebackend.modules.shared.dto.ModuleOverviewResponse;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/modules/auth")
@RequiredArgsConstructor
/**
 * Flow note: AuthModuleController belongs to the authentication module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public class AuthModuleController {

    private final AuthModuleService authModuleService;

    @Hidden
    @Deprecated(since = "2026-03", forRemoval = false)
    @PostMapping("/register")
    public ResponseEntity<ModuleAuthResponse> register(@Valid @RequestBody ModuleRegisterRequest request) {
        ModuleAuthResponse response = authModuleService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Hidden
    @Deprecated(since = "2026-03", forRemoval = false)
    @PostMapping("/login")
    public ResponseEntity<ModuleAuthResponse> login(@Valid @RequestBody ModuleLoginRequest request) {
        return ResponseEntity.ok(authModuleService.login(request));
    }

    @GetMapping("/overview")
    public ResponseEntity<ModuleOverviewResponse> getOverview() {
        return ResponseEntity.ok(authModuleService.getOverview());
    }
}