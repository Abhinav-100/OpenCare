package com.ciphertext.opencarebackend.modules.provider.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO for doctor degree information.
 * Contains complete degree details including relationships.
 */
@Getter
@Setter
/**
 * Flow note: DoctorDegreeResponse belongs to the provider doctor/hospital module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public class DoctorDegreeResponse {
    private Long id;
    private DoctorResponse doctor;
    private DegreeResponse degree;
    private MedicalSpecialityResponse medicalSpeciality;
    private InstitutionResponse institution;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private String grade;
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
