package com.ciphertext.opencarebackend.modules.shared.dto;

import java.util.List;

public record ModuleOverviewResponse(
        String module,
        String status,
        List<String> capabilities
) {
}