package com.ciphertext.opencarebackend.modules.provider.dto.filter;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AssociationFilter {
    private String name;
    private String bnName;
    private Integer medicalSpecialityId;
    private String associationType;
    private String domain;
    private Integer districtId;
    private Integer upazilaId;
}