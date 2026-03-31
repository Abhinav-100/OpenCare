package com.ciphertext.opencarebackend.modules.content.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RealTimeStatsDto {
    private Integer onlineUsers;
    private Integer activeAmbulances;
    private Integer pendingAppointments;
    private Integer criticalAlerts;
    private Double systemLoad;
    private Double memoryUsage;
}