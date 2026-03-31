package com.ciphertext.opencarebackend.modules.shared.dto.response.enums;

public record PermissionResponse(
        String value,
        String displayName,
        String banglaName,
        String description
) {}