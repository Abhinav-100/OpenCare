package com.ciphertext.opencarebackend.modules.provider.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
/**
 * Flow note: HospitalMedicalTestRequest belongs to the provider doctor/hospital module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public class HospitalMedicalTestRequest {
    private Long hospitalId;
    @NotNull(message = "Medical test ID is required")
    @Positive(message = "Medical test ID must be positive")
    private Integer medicalTestId;
    private String name;
    private String testCode;
    private String category;
    private String description;
    private Integer price;
    private Boolean isAvailable;
    private Integer processingTimeMinutes;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime sampleCollectedTime;

    private Boolean isActive ;
}