package com.ciphertext.opencarebackend.modules.appointment.controller;
import com.ciphertext.opencarebackend.modules.appointment.dto.filter.AppointmentFilter;
import com.ciphertext.opencarebackend.modules.appointment.dto.request.AppointmentRequest;
import com.ciphertext.opencarebackend.modules.appointment.dto.response.AppointmentResponse;
import com.ciphertext.opencarebackend.entity.Appointment;
import com.ciphertext.opencarebackend.enums.AppointmentStatus;
import com.ciphertext.opencarebackend.mapper.AppointmentMapper;
import com.ciphertext.opencarebackend.modules.appointment.service.AppointmentService;
import com.ciphertext.opencarebackend.modules.appointment.service.AppointmentService.AvailableSlot;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Tag(name = "Appointment Management", description = "API for managing doctor appointments including booking, cancellation, and slot availability")
public class AppointmentApiController {

    private final AppointmentService appointmentService;
    private final AppointmentMapper appointmentMapper;

    @Operation(
            summary = "Get paginated appointments",
            description = "Retrieves a paginated list of appointments with optional filters",
            parameters = {
                    @Parameter(name = "page", description = "Page number (0-based)", example = "0"),
                    @Parameter(name = "size", description = "Number of items per page", example = "10")
            }
    )
    @GetMapping("")
    public ResponseEntity<Map<String, Object>> getAllAppointmentsPage(
            @RequestParam(required = false) String appointmentNumber,
            @RequestParam(required = false) Long patientProfileId,
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) String appointmentType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate appointmentDateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate appointmentDateTo,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String paymentStatus,
            @RequestParam(required = false) Integer hospitalId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "appointmentDate") String sort,
            @RequestParam(defaultValue = "DESC") String direction) {

        // Build paging + sorting from query params.
        Sort.Direction sortDir = Sort.Direction.fromOptionalString(direction.toUpperCase()).orElse(Sort.Direction.DESC);
        Pageable pagingSort = PageRequest.of(page, size, Sort.by(sortDir, sort));

        // Build dynamic filter object passed to the service layer.
        AppointmentFilter filter = AppointmentFilter.builder()
                .appointmentNumber(appointmentNumber)
                .patientProfileId(patientProfileId)
                .doctorId(doctorId)
                .appointmentType(appointmentType)
                .appointmentDateFrom(appointmentDateFrom)
                .appointmentDateTo(appointmentDateTo)
                .status(status)
                .paymentStatus(paymentStatus)
                .hospitalId(hospitalId)
                .build();

        // Service returns entities; controller maps to API-safe response DTOs.
        Page<Appointment> pageAppointments = appointmentService.getPaginatedDataWithFilters(filter, pagingSort);
        Page<AppointmentResponse> responses = pageAppointments.map(appointmentMapper::toResponse);

        Map<String, Object> response = new HashMap<>();
        response.put("appointments", responses.getContent());
        response.put("currentPage", pageAppointments.getNumber());
        response.put("totalItems", pageAppointments.getTotalElements());
        response.put("totalPages", pageAppointments.getTotalPages());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get my appointments", description = "Retrieves all appointments for the currently authenticated user")
    @GetMapping("/my")
    public ResponseEntity<List<AppointmentResponse>> getMyAppointments(@AuthenticationPrincipal Jwt jwt) {
        String keycloakUserId = jwt.getSubject();
        List<Appointment> appointments = appointmentService.getMyAppointments(keycloakUserId);
        List<AppointmentResponse> responses = appointments.stream()
                .map(appointmentMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Get appointment by ID", description = "Retrieves an appointment by its unique ID")
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponse> getAppointmentById(@PathVariable Long id) {
        Appointment appointment = appointmentService.getAppointmentById(id);
        AppointmentResponse response = appointmentMapper.toResponse(appointment);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get available slots", description = "Retrieves available appointment slots for a doctor on a specific date")
    @GetMapping("/doctor/{doctorId}/slots")
    public ResponseEntity<List<AvailableSlot>> getAvailableSlots(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<AvailableSlot> slots = appointmentService.getAvailableSlots(doctorId, date);
        return ResponseEntity.ok(slots);
    }

    @Operation(summary = "Create appointment", description = "Books a new appointment for the authenticated user")
    @PostMapping
    public ResponseEntity<AppointmentResponse> createAppointment(
            @Valid @RequestBody AppointmentRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        // Authenticated subject becomes the patient owner of this booking.
        String keycloakUserId = jwt.getSubject();
        Appointment appointment = appointmentMapper.toEntity(request);
        appointment = appointmentService.createAppointment(appointment, keycloakUserId);
        AppointmentResponse response = appointmentMapper.toResponse(appointment);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Update appointment", description = "Updates an existing appointment")
    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponse> updateAppointment(
            @Valid @RequestBody AppointmentRequest request,
            @PathVariable Long id) {
        Appointment appointment = appointmentMapper.toEntity(request);
        appointment = appointmentService.updateAppointment(appointment, id);
        AppointmentResponse response = appointmentMapper.toResponse(appointment);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update appointment status", description = "Updates the status of an appointment (CONFIRMED, COMPLETED, NO_SHOW)")
    @PatchMapping("/{id}/status")
    public ResponseEntity<AppointmentResponse> updateAppointmentStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        AppointmentStatus appointmentStatus = AppointmentStatus.valueOf(status.toUpperCase());
        Appointment appointment = appointmentService.updateAppointmentStatus(id, appointmentStatus);
        AppointmentResponse response = appointmentMapper.toResponse(appointment);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Cancel appointment", description = "Cancels an appointment with a reason")
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelAppointment(
            @PathVariable Long id,
            @RequestParam(required = false) String reason,
            @AuthenticationPrincipal Jwt jwt) {
        // Store who cancelled for audit/debugging.
        String cancelledBy = jwt.getClaimAsString("preferred_username");
        appointmentService.cancelAppointment(id, reason, cancelledBy);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete appointment", description = "Permanently deletes an appointment")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteAppointmentById(id);
        return ResponseEntity.noContent().build();
    }
}
