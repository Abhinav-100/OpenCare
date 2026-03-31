package com.ciphertext.opencarebackend.modules.provider.dto.response;
import com.ciphertext.opencarebackend.modules.provider.dto.response.enums.AssociationTypeResponse;
import com.ciphertext.opencarebackend.modules.catalog.dto.response.enums.CountryResponse;
import com.ciphertext.opencarebackend.modules.catalog.dto.response.enums.DomainResponse;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import com.ciphertext.opencarebackend.modules.shared.dto.response.DistrictResponse;
import com.ciphertext.opencarebackend.modules.shared.dto.response.DivisionResponse;
import com.ciphertext.opencarebackend.modules.shared.dto.response.UpazilaResponse;

@Getter
@Setter
public class AssociationResponse {

    private Integer id;
    private String name;
    private String bnName;
    private String shortName;
    private AssociationTypeResponse associationType;

    private MedicalSpecialityResponse medicalSpeciality;

    private String description;
    private String logoUrl;
    private LocalDate foundedDate;

    private String websiteUrl;
    private String facebookUrl;
    private String twitterUrl;
    private String linkedinUrl;
    private String youtubeUrl;

    private String email;
    private String phone;

    private Long divisionId;
    private DivisionResponse division;

    private Long districtId;
    private DistrictResponse district;

    private Long upazilaId;
    private UpazilaResponse upazila;

    private CountryResponse originCountry;

    private DomainResponse domain;

    private Boolean isAffiliated;
    private Boolean isActive;

    private List<DoctorAssociationResponse> doctorAssociations;
}