package com.ciphertext.opencarebackend.modules.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Flow note: ModuleRegisterRequest belongs to the authentication module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public record ModuleRegisterRequest(
        @Email @NotBlank String email,
        @NotBlank @Size(min = 8, max = 100) String password,
        @NotBlank @Size(max = 100) String firstName,
        @NotBlank @Size(max = 100) String lastName,
        @Size(max = 20) String phone,
        String role
) {
}