package com.ciphertext.opencarebackend.modules.provider.dto.filter;

import lombok.Builder;
import lombok.Getter;


import java.util.List;


@Builder
@Getter
public class NurseFilter {
    private String name;
    private String bnName;
    private String bnmcNo;
    private Integer minExperience;
    private Integer maxExperience;
    private Boolean isVerified;
    private Boolean isActive;
    private Integer districtId;
    private Integer upazilaId;
    private Integer unionId;
    private List<Integer> districtIds;
    private List<Integer> upazilaIds;
    private List<Integer> unionIds;
}