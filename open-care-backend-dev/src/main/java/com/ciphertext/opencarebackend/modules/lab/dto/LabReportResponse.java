package com.ciphertext.opencarebackend.modules.lab.dto;

import java.time.LocalDateTime;

public record LabReportResponse(
        Long reportId,
        Long labTestId,
        Long patientId,
        String reportType,
        String fileUrl,
        String status,
        LocalDateTime reportedAt
) {
}