package com.ciphertext.opencarebackend.modules.provider.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
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