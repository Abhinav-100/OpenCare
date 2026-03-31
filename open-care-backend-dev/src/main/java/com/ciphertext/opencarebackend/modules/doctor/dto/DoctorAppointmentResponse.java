package com.ciphertext.opencarebackend.modules.doctor.dto;

import java.time.LocalDateTime;

public record DoctorAppointmentResponse(
        Long id,
        String appointmentNumber,
        Long patientId,
        LocalDateTime scheduledAt,
        Integer durationMinutes,
        String status,
        String reason,
        String diagnosis
) {
}