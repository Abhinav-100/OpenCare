package com.ciphertext.opencarebackend.modules.provider.dto.filter;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AmbulanceFilter {
    private String vehicleNumber;
    private String type;
    private String driverName;
    private String driverPhone;
    private Boolean isAvailable;
    private Boolean isAffiliated;
    private Integer hospitalId;
    private Integer upazilaId;
    private Integer districtId;
    private Boolean isActive;
}