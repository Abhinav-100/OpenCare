package com.ciphertext.opencarebackend.modules.provider.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MedicalSpecialityRequest {
    private Integer id; // null for create, set for update
    private Integer parentId;
    private String name;
    private String bnName;
    private String icon;
    private String imageUrl;
    private String description;
    private Integer doctorCount;
}