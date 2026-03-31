package com.ciphertext.opencarebackend.modules.patient.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record PatientRegisterRequest(
        @Email @NotBlank String email,
        @NotBlank @Size(min = 8, max = 100) String password,
        @NotBlank @Size(max = 100) String firstName,
        @NotBlank @Size(max = 100) String lastName,
        @Size(max = 20) String phone,
        @NotNull LocalDate dateOfBirth,
        @NotBlank String gender,
        String bloodGroup,
        String address,
        String emergencyContactName,
        String emergencyContactPhone
) {
}