package com.ciphertext.opencarebackend.modules.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
/**
 * Flow note: LoginRequest belongs to the authentication module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public class LoginRequest {
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;
}