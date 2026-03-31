package com.ciphertext.opencarebackend.modules.provider.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MedicalSpecialityResponse {
    private Integer id;
    private Integer parentId;
    private String name;
    private String bnName;
    private String icon;
    private String imageUrl;
    private String description;
    private Integer doctorCount;
}