package com.ciphertext.opencarebackend.modules.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ModuleLoginRequest(
        @Email @NotBlank String email,
        @NotBlank String password
) {
}