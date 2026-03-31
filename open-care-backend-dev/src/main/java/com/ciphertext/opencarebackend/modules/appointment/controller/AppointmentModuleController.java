package com.ciphertext.opencarebackend.modules.appointment.controller;
import com.ciphertext.opencarebackend.modules.appointment.dto.request.AppointmentRequest;
import com.ciphertext.opencarebackend.modules.appointment.dto.response.AppointmentResponse;
import com.ciphertext.opencarebackend.enums.AppointmentStatus;
import com.ciphertext.opencarebackend.modules.appointment.service.AppointmentModuleService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modules/appointments")
@RequiredArgsConstructor
@Tag(name = "Appointment Module", description = "Modular appointment endpoints")
public class AppointmentModuleController {

    private final AppointmentModuleService appointmentModuleService;

    @Hidden
    @Deprecated(since = "2026-03", forRemoval = false)
    @GetMapping("/my")
    @Operation(summary = "Get my appointments", description = "Fetch appointments for authenticated user through module layer")
    public ResponseEntity<List<AppointmentResponse>> getMyAppointments(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(appointmentModuleService.getMyAppointments(jwt.getSubject()));
    }

    @Hidden
    @Deprecated(since = "2026-03", forRemoval = false)
    @PostMapping
    @Operation(summary = "Create appointment", description = "Create appointment through module service layer")
    public ResponseEntity<AppointmentResponse> createAppointment(
            @Valid @RequestBody AppointmentRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        AppointmentResponse response = appointmentModuleService.createAppointment(request, jwt.getSubject());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Hidden
    @Deprecated(since = "2026-03", forRemoval = false)
    @PatchMapping("/{id}/status")
    @Operation(summary = "Update appointment status", description = "Update appointment status through module service layer")
    public ResponseEntity<AppointmentResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam AppointmentStatus status
    ) {
        return ResponseEntity.ok(appointmentModuleService.updateAppointmentStatus(id, status));
    }
}
