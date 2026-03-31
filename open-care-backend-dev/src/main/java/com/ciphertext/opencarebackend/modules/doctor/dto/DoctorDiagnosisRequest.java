package com.ciphertext.opencarebackend.modules.doctor.dto;

import jakarta.validation.constraints.NotBlank;

public record DoctorDiagnosisRequest(
        @NotBlank String diagnosis,
        String notes
) {
}