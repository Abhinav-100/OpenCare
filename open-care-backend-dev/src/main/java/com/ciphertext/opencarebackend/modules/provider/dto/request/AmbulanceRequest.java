package com.ciphertext.opencarebackend.modules.provider.dto.request;

import com.ciphertext.opencarebackend.config.RegionDefaults;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AmbulanceRequest {
    @NotBlank(message = "Vehicle number is required")
    @Size(max = 20, message = "Vehicle number must be at most 20 characters")
    private String vehicleNumber;

    @Pattern(
            regexp = "^(BASIC|ADVANCED|NEONATAL|PEDIATRIC|AIR_AMBULANCE)$",
            message = "Invalid ambulance type"
    )
    private String type;

    @Size(max = 100, message = "Driver name must be at most 100 characters")
    private String driverName;

    @Pattern(regexp = RegionDefaults.INDIAN_PHONE_REGEX, message = RegionDefaults.INDIAN_PHONE_MESSAGE)
    private String driverPhone;

    private Boolean isAvailable;
    private Boolean isAffiliated;

    @Positive(message = "Hospital ID must be a positive number")
    private Integer hospitalId;

    @Positive(message = "Upazila ID must be a positive number")
    private Integer upazilaId;

    @Positive(message = "District ID must be a positive number")
    private Integer districtId;

    private Boolean isActive;
}