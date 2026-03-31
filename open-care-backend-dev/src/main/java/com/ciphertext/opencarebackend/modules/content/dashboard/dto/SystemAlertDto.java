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
public class SystemAlertDto {
    private Long id;
    private String title;
    private String message;
    private String severity; // HIGH, MEDIUM, LOW
    private LocalDateTime timestamp;
    private String category;
    private boolean acknowledged;
}