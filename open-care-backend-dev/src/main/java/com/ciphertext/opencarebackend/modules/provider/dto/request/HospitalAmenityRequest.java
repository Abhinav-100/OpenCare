package com.ciphertext.opencarebackend.modules.provider.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HospitalAmenityRequest {
    private Long hospitalId;
    private String type;
    private String name;
    private Integer price;
    private Integer quantity;
    private Integer available;
    private Boolean isActive;

}