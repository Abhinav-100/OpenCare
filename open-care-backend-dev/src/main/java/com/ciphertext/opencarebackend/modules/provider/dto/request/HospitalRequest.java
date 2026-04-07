package com.ciphertext.opencarebackend.modules.provider.dto.request;

import com.ciphertext.opencarebackend.config.RegionDefaults;
import com.ciphertext.opencarebackend.enums.HospitalType;
import com.ciphertext.opencarebackend.enums.OrganizationType;
import com.ciphertext.opencarebackend.validation.EnumNamePattern;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
/**
 * Flow note: HospitalRequest belongs to the provider doctor/hospital module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public class HospitalRequest {
    private Integer id;

    @NotBlank(message = "Hospital name is required")
    @Size(max = 100, message = "Hospital name must not exceed 100 characters")
    private String name;

    @Size(max = 100, message = "Bengali name must not exceed 100 characters")
    private String bnName;

    @Size(max = 250, message = "Image URL must not exceed 250 characters")
    @Pattern(regexp = "^(https?://.*)?$", message = "Invalid image URL format")
    private String imageUrl;

    @Min(value = 0, message = "Number of beds must be at least 0")
    @Max(value = 100000, message = "Number of beds seems unrealistic")
    private Integer numberOfBed;

    @NotNull(message = "District is required")
    private Integer districtId;

    private Integer upazilaId;

    private Integer unionId;

    @EnumNamePattern(enumClass = HospitalType.class, message = "Invalid hospital type")
    private String hospitalType;

    @EnumNamePattern(enumClass = OrganizationType.class, message = "Invalid organization type")
    private String organizationType;

    @DecimalMin(value = "-90.0", message = "Latitude must be >= -90.0")
    @DecimalMax(value = "90.0", message = "Latitude must be <= 90.0")
    @Digits(integer = 3, fraction = 9, message = "Latitude must have up to 9 decimal places")
    private BigDecimal lat;

    @DecimalMin(value = "-180.0", message = "Longitude must be >= -180.0")
    @DecimalMax(value = "180.0", message = "Longitude must be <= 180.0")
    @Digits(integer = 3, fraction = 9, message = "Longitude must have up to 9 decimal places")
    private BigDecimal lon;

    @Size(max = 500, message = "Website URL must not exceed 500 characters")
    @Pattern(regexp = "^(https?://.*)?$", message = "Invalid website URL format")
    private String websiteUrl;

    @Size(max = 50, message = "Registration code must not exceed 50 characters")
    private String registrationCode;

    @Size(max = 150, message = "Slug must not exceed 150 characters")
    private String slug;

    @Size(max = 500, message = "Facebook page URL must not exceed 500 characters")
    @Pattern(regexp = "^(https?://.*)?$", message = "Invalid Facebook page URL format")
    private String facebookPageUrl;

    @Size(max = 500, message = "Twitter profile URL must not exceed 500 characters")
    @Pattern(regexp = "^(https?://.*)?$", message = "Invalid Twitter profile URL format")
    private String twitterProfileUrl;

    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @Pattern(regexp = RegionDefaults.INDIAN_PHONE_REGEX, message = RegionDefaults.INDIAN_PHONE_MESSAGE)
    private String phone;

    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;

    private Boolean hasEmergencyService;

    private Boolean hasAmbulanceService;

    private Boolean hasBloodBank;

    private Boolean isAffiliated;

    private Boolean isActive;

    @Size(max = 20, message = "Cannot assign more than 20 tags")
    private Set<Integer> tagIds;

    @AssertTrue(message = "Latitude and longitude must be provided together")
    private boolean isLatLonPairValid() {
        return (lat == null && lon == null) || (lat != null && lon != null);
    }
}