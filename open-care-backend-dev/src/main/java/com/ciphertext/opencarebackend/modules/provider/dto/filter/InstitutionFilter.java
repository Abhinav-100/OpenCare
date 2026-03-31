package com.ciphertext.opencarebackend.modules.provider.dto.filter;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class InstitutionFilter {
    private String name;
    private String bnName;
    private Integer enroll;
    private String country;
    private List<Integer> districtIds;
    private List<String> institutionTypes;
    private String organizationType;
}