package com.ciphertext.opencarebackend.modules.lab.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record LabReportUploadRequest(
        @NotNull @Positive Long labTestId,
        @NotBlank String reportType,
        @NotBlank String fileUrl,
        String summary
) {
}