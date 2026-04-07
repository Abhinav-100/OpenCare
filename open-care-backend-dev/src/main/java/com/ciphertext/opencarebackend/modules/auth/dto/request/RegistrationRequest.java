package com.ciphertext.opencarebackend.modules.auth.dto.request;

import com.ciphertext.opencarebackend.config.RegionDefaults;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
/**
 * Flow note: RegistrationRequest belongs to the authentication module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public class RegistrationRequest {
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
}