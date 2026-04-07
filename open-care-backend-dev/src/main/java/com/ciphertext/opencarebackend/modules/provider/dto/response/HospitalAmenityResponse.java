package com.ciphertext.opencarebackend.modules.provider.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
/**
 * Flow note: HospitalAmenityResponse belongs to the provider doctor/hospital module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public class HospitalAmenityResponse {
    private Long id;
    private HospitalResponse hospital;
    private String type;
    private String name;
    private Integer price;
    private Integer quantity;
    private Integer available;
    private Boolean isActive;
}