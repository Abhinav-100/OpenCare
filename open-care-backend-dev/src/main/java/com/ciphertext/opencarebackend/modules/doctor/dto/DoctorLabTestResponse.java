package com.ciphertext.opencarebackend.modules.doctor.dto;

import java.time.LocalDateTime;

public record DoctorLabTestResponse(
        Long id,
        Long appointmentId,
        Long patientId,
        Long doctorId,
        String testCode,
        String testName,
        String status,
        LocalDateTime requestedAt
) {
}