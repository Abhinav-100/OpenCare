package com.ciphertext.opencarebackend.modules.content.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsDataDto {
    private Integer totalUsers;
    private Integer activeUsers;
    private Integer appointmentsToday;
    private Integer emergencyCalls;
    private Double systemUptime;
    private Integer averageResponseTime;
}