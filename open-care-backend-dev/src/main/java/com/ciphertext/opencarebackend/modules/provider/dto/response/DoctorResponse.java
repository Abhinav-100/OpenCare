package com.ciphertext.opencarebackend.modules.provider.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import com.ciphertext.opencarebackend.modules.catalog.dto.response.TagResponse;
import com.ciphertext.opencarebackend.modules.user.dto.response.ProfileResponse;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
/**
 * Flow note: DoctorResponse belongs to the provider doctor/hospital module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public class DoctorResponse {
    private Long id;
    private String bmdcNo;
    private LocalDate startDate;
    private Integer yearOfExperience;
    private String degrees;
    private String specializations;
    private String description;
    private BigDecimal consultationFeeOnline;
    private BigDecimal consultationFeeOffline;
    private Boolean isActive;
    private Boolean isVerified;
    private ProfileResponse profile;
    private Integer hospitalId;
    private String hospitalName;
    private Set<TagResponse> tags;

    // Optional fields - populated based on query parameters
    private List<DoctorDegreeResponse> doctorDegrees;
    private List<DoctorWorkplaceResponse> doctorWorkplaces;
    private List<DoctorAssociationResponse> doctorAssociations;

    // Audit fields
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
}