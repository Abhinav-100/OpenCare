package com.ciphertext.opencarebackend.modules.provider.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DoctorWorkplaceRequest {
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

    @PastOrPresent(message = "Start date cannot be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @PastOrPresent(message = "End date cannot be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @AssertTrue(message = "End date must be after start date")
    public boolean isEndDateValid() {
        if (endDate == null || startDate == null) {
            return true;
        }
        return endDate.isAfter(startDate) || endDate.isEqual(startDate);
    }
}