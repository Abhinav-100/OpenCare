package com.ciphertext.opencarebackend.modules.blood.dto.filter;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class BloodBankFilter {
    private String name;
    private Integer hospitalId;
    private Boolean isAlwaysOpen;
    private Boolean isActive;
    private String bloodGroupNeeded;
}