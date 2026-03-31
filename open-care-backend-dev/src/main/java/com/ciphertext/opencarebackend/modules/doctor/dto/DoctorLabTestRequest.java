package com.ciphertext.opencarebackend.modules.doctor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record DoctorLabTestRequest(
        @NotNull @Positive Long appointmentId,
        @NotBlank String testCode,
        @NotBlank String testName,
        String notes
) {
}