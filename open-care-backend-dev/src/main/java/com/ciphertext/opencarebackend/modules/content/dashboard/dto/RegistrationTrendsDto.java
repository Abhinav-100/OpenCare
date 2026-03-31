package com.ciphertext.opencarebackend.modules.content.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationTrendsDto {
    private String[] months;
    private Integer[] doctors;
    private Integer[] nurses;
    private Integer[] hospitals;
    private Integer[] patients;
}