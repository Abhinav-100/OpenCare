package com.ciphertext.opencarebackend.modules.provider.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Request DTO for batch create/update doctor workplace operations.
 * If id is provided, it performs an update; otherwise, it creates a new workplace.
 *
 * @author Sadman
 */
@Getter
@Setter
/**
 * Flow note: DoctorWorkplaceBatchRequest belongs to the provider doctor/hospital module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public class DoctorWorkplaceBatchRequest {

    /**
     * If provided, updates existing workplace; if null, creates new workplace
     */
    private Long id;

    @NotNull(message = "Doctor ID is required")
    @Positive(message = "Doctor ID must be positive")
    private Long doctorId;

    @Positive(message = "Medical speciality ID must be positive")
    private Long medicalSpecialityId;

    @Positive(message = "Institution ID must be positive")
    private Long institutionId;

    @Positive(message = "Hospital ID must be positive")
    private Long hospitalId;

    @Size(max = 100, message = "Doctor position cannot exceed 100 characters")
    private String doctorPosition;

    @Size(max = 50, message = "Teacher position cannot exceed 50 characters")
    private String teacherPosition;

    @NotNull(message = "Start date is required")
    @PastOrPresent(message = "Start date cannot be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    /**
     * Custom validation to ensure end date is after start date.
     */
    @AssertTrue(message = "End date must be after start date")
    public boolean isEndDateValid() {
        if (endDate == null || startDate == null) {
            return true;
        }
        return endDate.isAfter(startDate) || endDate.isEqual(startDate);
    }

    /**
     * Determines if this is an update operation (has id) or create operation (no id).
     */
    public boolean isUpdate() {
        return id != null;
    }
}