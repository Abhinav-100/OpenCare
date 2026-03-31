package com.ciphertext.opencarebackend.modules.auth.dto.request;

import com.ciphertext.opencarebackend.config.RegionDefaults;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DoctorSelfRegistrationRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @NotBlank(message = "Phone number is required")
        @Pattern(regexp = RegionDefaults.INDIAN_PHONE_REGEX, message = RegionDefaults.INDIAN_PHONE_MESSAGE)
    private String phone;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$",
            message = "Password must contain at least one digit, one lowercase, one uppercase, and one special character")
    private String password;

    @NotBlank(message = "Blood group is required")
    @Pattern(regexp = "^(A_POSITIVE|A_NEGATIVE|B_POSITIVE|B_NEGATIVE|O_POSITIVE|O_NEGATIVE|AB_POSITIVE|AB_NEGATIVE)$",
            message = "Invalid blood group")
    private String bloodGroup;

    @NotBlank(message = "Gender is required")
    @Pattern(regexp = "^(MALE|FEMALE|OTHER)$", message = "Invalid gender value")
    private String gender;

    @NotNull(message = "District ID is required")
    @Positive(message = "District ID must be a positive number")
    private Integer districtId;

    @Size(max = 50, message = "BMDC number must not exceed 50 characters")
    private String bmdcNo;

    @Size(max = 500, message = "Degrees must not exceed 500 characters")
    private String degrees;

    @Size(max = 500, message = "Specializations must not exceed 500 characters")
    private String specializations;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @DecimalMin(value = "0.0", message = "Online consultation fee cannot be negative")
    private BigDecimal consultationFeeOnline;

    @DecimalMin(value = "0.0", message = "Offline consultation fee cannot be negative")
    private BigDecimal consultationFeeOffline;

    @Positive(message = "Hospital ID must be positive")
    private Integer hospitalId;
}