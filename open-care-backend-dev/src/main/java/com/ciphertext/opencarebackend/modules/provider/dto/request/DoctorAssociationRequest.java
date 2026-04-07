package com.ciphertext.opencarebackend.modules.provider.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
/**
 * Flow note: DoctorAssociationRequest belongs to the provider doctor/hospital module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public class DoctorAssociationRequest {
    private Long doctorId;

    @NotNull(message = "Association ID is required")
    @Positive(message = "Association ID must be positive")
    private Integer associationId;
    private String membershipType;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isActive;
}