package com.ciphertext.opencarebackend.modules.patient.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record PatientAppointmentBookingRequest(
        @NotNull @Positive Long doctorId,
        @NotNull @Future LocalDateTime scheduledAt,
        @NotNull @Positive Integer durationMinutes,
        String reason
) {
}