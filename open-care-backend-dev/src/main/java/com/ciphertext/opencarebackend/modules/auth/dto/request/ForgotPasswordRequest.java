package com.ciphertext.opencarebackend.modules.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
/**
 * Flow note: ForgotPasswordRequest belongs to the authentication module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public class ForgotPasswordRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
}