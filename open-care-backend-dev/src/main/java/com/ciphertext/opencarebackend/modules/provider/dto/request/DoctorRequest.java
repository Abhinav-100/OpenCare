package com.ciphertext.opencarebackend.modules.provider.dto.request;

import com.ciphertext.opencarebackend.config.RegionDefaults;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
/**
 * Flow note: DoctorRequest belongs to the provider doctor/hospital module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public class DoctorRequest {
    private Long id;

    @Size(max = 50, message = "BMDC number must not exceed 50 characters")
    private String bmdcNo;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Start date must be in format YYYY-MM-DD")
    private String startDate;

    @Size(max = 500, message = "Degrees must not exceed 500 characters")
    private String degrees;

    @Size(max = 500, message = "Specializations must not exceed 500 characters")
    private String specializations;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    private Boolean isActive;

    private Boolean isVerified;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Username can only contain letters, numbers, hyphens and underscores")
    private String username;

    @Size(max = 255, message = "Photo URL must not exceed 255 characters")
    @Pattern(regexp = "^(https?://.*)?$", message = "Invalid photo URL format")
    private String photo;

    @Pattern(regexp = RegionDefaults.INDIAN_PHONE_REGEX, message = RegionDefaults.INDIAN_PHONE_MESSAGE)
    private String phone;

    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @Size(max = 100, message = "Bengali name must not exceed 100 characters")
    private String bnName;

    @NotBlank(message = "Gender is required")
    @Pattern(regexp = "MALE|FEMALE|OTHER", message = "Gender must be MALE, FEMALE, or OTHER")
    private String gender;

    @PastOrPresent(message = "Date of birth cannot be in the future")
    private LocalDate dateOfBirth;

    @Pattern(regexp = "A_POSITIVE|A_NEGATIVE|B_POSITIVE|B_NEGATIVE|AB_POSITIVE|AB_NEGATIVE|O_POSITIVE|O_NEGATIVE", message = "Invalid blood group")
    private String bloodGroup;

    private String facebookProfileUrl;

    private String linkedinProfileUrl;

    private String researchGateProfileUrl;

    private Boolean isVolunteer;

    private Boolean healthDataConsent;

    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;

    private Integer districtId;

    private Integer upazilaId;

    private Integer unionId;

    @Positive(message = "Hospital ID must be positive")
    private Integer hospitalId;

    @Size(max = 20, message = "Cannot assign more than 20 tags")
    private Set<Integer> tagIds;
}