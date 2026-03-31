package com.ciphertext.opencarebackend.modules.provider.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Request DTO for creating or updating doctor degree information.
 * Contains validation rules to ensure data integrity.
 */
@Getter
@Setter
public class DoctorDegreeRequest {

    @NotNull(message = "Doctor ID is required")
    @Positive(message = "Doctor ID must be positive")
    private Long doctorId;

    @NotNull(message = "Degree ID is required")
    @Positive(message = "Degree ID must be positive")
    private Long degreeId;

    @Positive(message = "Medical speciality ID must be positive")
    private Long medicalSpecialityId;

    @NotNull(message = "Institution ID is required")
    @Positive(message = "Institution ID must be positive")
    private Long institutionId;

    @NotNull(message = "Start date is required")
    @PastOrPresent(message = "Start date cannot be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @PastOrPresent(message = "End date cannot be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Size(max = 50, message = "Grade cannot exceed 50 characters")
    private String grade;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    /**
     * Custom validation to ensure end date is after start date.
     */
    @AssertTrue(message = "End date must be after start date")
    public boolean isEndDateValid() {
        if (endDate == null || startDate == null) {
            return true; // Let @NotNull handle null validation
        }
        return endDate.isAfter(startDate) || endDate.isEqual(startDate);
    }
}