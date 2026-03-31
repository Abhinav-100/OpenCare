package com.ciphertext.opencarebackend.modules.provider.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MedicalTestRequest {
    private Integer parentId;
    private String name;
    private String bnName;
    private String alternativeNames;
    private String description;
}
