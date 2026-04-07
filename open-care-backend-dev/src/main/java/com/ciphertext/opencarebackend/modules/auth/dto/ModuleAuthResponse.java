package com.ciphertext.opencarebackend.modules.auth.dto;

import java.util.List;

/**
 * Flow note: ModuleAuthResponse belongs to the authentication module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public record ModuleAuthResponse(
        String accessToken,
        String tokenType,
        long expiresInSeconds,
        String email,
        List<String> roles
) {
}