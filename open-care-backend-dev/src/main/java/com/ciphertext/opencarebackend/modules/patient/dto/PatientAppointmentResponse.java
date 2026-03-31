package com.ciphertext.opencarebackend.modules.patient.dto;

import java.time.LocalDateTime;

public record PatientAppointmentResponse(
        Long id,
        String appointmentNumber,
        Long doctorId,
        LocalDateTime scheduledAt,
        Integer durationMinutes,
        String status,
        String reason,
        String diagnosis
) {
}