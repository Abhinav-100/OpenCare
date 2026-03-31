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
public class DashboardOverviewResponse {
    private Long totalDoctors;
    private Long totalNurses;
    private Long totalPatients;
    private Long totalAdmins;
    private Long totalHospitals;
    private Long totalInstitutions;
    private Long bloodUnitsAvailable;
    private Long ambulancesActive;
    private Long associationsRegistered;
    private Long socialOrganizations;
    private LocalDateTime lastUpdated;
}