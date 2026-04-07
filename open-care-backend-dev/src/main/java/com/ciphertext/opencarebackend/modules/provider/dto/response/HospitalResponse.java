package com.ciphertext.opencarebackend.modules.provider.dto.response;
import com.ciphertext.opencarebackend.modules.provider.dto.response.enums.HospitalTypeResponse;
import com.ciphertext.opencarebackend.modules.shared.dto.response.enums.OrganizationTypeResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.ciphertext.opencarebackend.modules.catalog.dto.response.TagResponse;
import com.ciphertext.opencarebackend.modules.shared.dto.response.DistrictResponse;
import com.ciphertext.opencarebackend.modules.shared.dto.response.UnionResponse;
import com.ciphertext.opencarebackend.modules.shared.dto.response.UpazilaResponse;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
/**
 * Flow note: HospitalResponse belongs to the provider doctor/hospital module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public class HospitalResponse {
    private Integer id;
    private String name;
    private String bnName;
    private String slug;
    private String imageUrl;
    private Integer numberOfBed;
    private DistrictResponse district;
    private UpazilaResponse upazila;
    private UnionResponse union;
    private HospitalTypeResponse hospitalType;
    private OrganizationTypeResponse organizationType;
    private BigDecimal lat;
    private BigDecimal lon;
    private String facebookPageUrl;
    private String twitterProfileUrl;
    private String websiteUrl;
    private String registrationCode;
    private String email;
    private String phone;
    private String address;
    private Boolean hasEmergencyService;
    private Boolean hasAmbulanceService;
    private Boolean hasBloodBank;
    private Boolean isAffiliated;
    private Boolean isVerified;
    private Boolean isActive;
    private Set<TagResponse> tags = new HashSet<>();
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    // Optional fields - populated based on query parameters
    private List<DoctorResponse> doctors;
    private List<HospitalMedicalTestResponse> tests;
    private List<HospitalAmenityResponse> amenities;
}