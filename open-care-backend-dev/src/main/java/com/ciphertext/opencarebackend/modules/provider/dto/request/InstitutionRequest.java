package com.ciphertext.opencarebackend.modules.provider.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
public class InstitutionRequest {
    private Integer id; // null for create, set for update
    private String acronym;
    private String name;
    private String bnName;
    private String imageUrl;
    private Integer establishedYear;
    private Integer enroll;
    private Integer districtId;
    private Integer upazilaId;
    private Integer affiliatedHospitalId;
    private String country;
    private String institutionType; // enum name
    private String organizationType; // enum name
    private BigDecimal lat;
    private BigDecimal lon;
    private String websiteUrl;
    private String email;
    private String phone;
    private String address;
    private Boolean affiliated;
    private Set<Integer> tagIds; // Tag IDs to associate with the institution
}