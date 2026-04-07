package com.ciphertext.opencarebackend.modules.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
/**
 * Flow note: RefreshTokenRequest belongs to the authentication module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public class RefreshTokenRequest {
    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}