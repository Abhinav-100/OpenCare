package com.ciphertext.opencarebackend.modules.patient.dto;

import java.time.LocalDateTime;

public record PatientReportResponse(
        Long reportId,
        Long labTestId,
        String reportType,
        String fileUrl,
        String summary,
        String status,
        LocalDateTime reportedAt
) {
}