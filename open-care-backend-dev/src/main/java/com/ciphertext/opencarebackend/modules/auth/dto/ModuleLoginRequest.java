package com.ciphertext.opencarebackend.modules.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Flow note: ModuleLoginRequest belongs to the authentication module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public record ModuleLoginRequest(
        @Email @NotBlank String email,
        @NotBlank String password
) {
}