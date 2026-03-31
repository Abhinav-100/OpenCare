package com.ciphertext.opencarebackend.modules.content.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLogDto {
    private Long id;
    private String entityType;
    private String action;
    private String status;
    private LocalDateTime timestamp;
    private String details;
    private String userId;
    private String ipAddress;
}