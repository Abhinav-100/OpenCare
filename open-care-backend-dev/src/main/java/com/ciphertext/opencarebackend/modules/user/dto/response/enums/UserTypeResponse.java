package com.ciphertext.opencarebackend.modules.user.dto.response.enums;

public record UserTypeResponse(
        String value,
        String displayName,
        String banglaName,
        String keycloakGroupName,
        String description
) {}