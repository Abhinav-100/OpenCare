package com.ciphertext.opencarebackend.modules.provider.dto.response;
import com.ciphertext.opencarebackend.modules.provider.dto.response.enums.AmbulanceTypeResponse;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import com.ciphertext.opencarebackend.modules.shared.dto.response.DistrictResponse;
import com.ciphertext.opencarebackend.modules.shared.dto.response.UpazilaResponse;

@Getter
@Setter
public class AmbulanceResponse {
    private Long id;
    private String vehicleNumber;
    private AmbulanceTypeResponse type;
    private String driverName;
    private String driverPhone;
    private Boolean isAvailable;
    private Boolean isAffiliated;
    private HospitalResponse hospital;
    private UpazilaResponse upazila;
    private DistrictResponse district;
    private Boolean isActive;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}