package com.ciphertext.opencarebackend.modules.provider.dto.request;

import com.ciphertext.opencarebackend.enums.MembershipType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Request DTO for batch create/update doctor association operations.
 * If id is provided, it performs an update; otherwise, it creates a new association.
 */
@Getter
@Setter
/**
 * Flow note: DoctorAssociationBatchRequest belongs to the provider doctor/hospital module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public class DoctorAssociationBatchRequest {

    /**
     * If provided, updates existing association; if null, creates new association
     */
    private Long id;

    @NotNull(message = "Doctor ID is required")
    @Positive(message = "Doctor ID must be positive")
    private Long doctorId;

    @NotNull(message = "Association ID is required")
    @Positive(message = "Association ID must be positive")
    private Integer associationId;

    @NotNull(message = "Membership type is required")
    private MembershipType membershipType;

    @NotNull(message = "Start date is required")
    @PastOrPresent(message = "Start date cannot be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @PastOrPresent(message = "End date cannot be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @NotNull(message = "Active status is required")
    private Boolean isActive;

    /**
     * Determines if this request is for an update operation.
     *
     * @return true if id is provided (update), false otherwise (create)
     */
    public boolean isUpdate() {
        return id != null;
    }
}