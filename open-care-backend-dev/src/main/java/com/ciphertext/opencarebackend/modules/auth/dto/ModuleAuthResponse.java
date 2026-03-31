package com.ciphertext.opencarebackend.modules.auth.dto;

import java.util.List;

public record ModuleAuthResponse(
        String accessToken,
        String tokenType,
        long expiresInSeconds,
        String email,
        List<String> roles
) {
}