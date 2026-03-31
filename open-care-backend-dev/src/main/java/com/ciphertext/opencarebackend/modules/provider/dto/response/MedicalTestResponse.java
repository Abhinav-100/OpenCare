package com.ciphertext.opencarebackend.modules.provider.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MedicalTestResponse {
    private Integer id;
    private Integer parentId;
    private String name;
    private String bnName;
    private String alternativeNames;
    private String description;
    private Integer hospitalCount;
}