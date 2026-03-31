package com.ciphertext.opencarebackend.modules.provider.dto.response;
import com.ciphertext.opencarebackend.modules.catalog.dto.response.enums.CountryResponse;
import com.ciphertext.opencarebackend.modules.provider.dto.response.enums.InstitutionTypeResponse;
import com.ciphertext.opencarebackend.modules.shared.dto.response.enums.OrganizationTypeResponse;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;
import com.ciphertext.opencarebackend.modules.catalog.dto.response.TagResponse;
import com.ciphertext.opencarebackend.modules.shared.dto.response.DistrictResponse;
import com.ciphertext.opencarebackend.modules.shared.dto.response.UpazilaResponse;

@Getter
@Setter
public class InstitutionResponse {
    private int id;
    private String acronym;
    private String name;
    private String bnName;
    private String imageUrl;
    private Integer establishedYear;
    private Integer enroll;
    private DistrictResponse district;
    private UpazilaResponse upazila;
    private HospitalResponse affiliatedHospital;
    private CountryResponse country;
    private InstitutionTypeResponse institutionType;
    private OrganizationTypeResponse organizationType;
    private BigDecimal lat;
    private BigDecimal lon;
    private String websiteUrl;
    private String email;
    private String phone;
    private String address;
    private boolean affiliated;
    private Set<TagResponse> tags;
    // Optional: WKT or GeoJSON representation if needed
    private String locationWkt;
}