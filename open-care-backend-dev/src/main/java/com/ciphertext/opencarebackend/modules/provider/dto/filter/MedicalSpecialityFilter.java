package com.ciphertext.opencarebackend.modules.provider.dto.filter;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MedicalSpecialityFilter {
    private String name;
    private String bnName;
    private Integer parentId;
    private String description;
}