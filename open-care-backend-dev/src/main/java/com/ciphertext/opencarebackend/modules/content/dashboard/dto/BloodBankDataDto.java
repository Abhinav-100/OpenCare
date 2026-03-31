package com.ciphertext.opencarebackend.modules.content.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BloodBankDataDto {
    private String[] bloodTypes;
    private Integer[] units;
    private Map<String, Integer> criticalLevels;
    private LocalDateTime lastUpdated;
}