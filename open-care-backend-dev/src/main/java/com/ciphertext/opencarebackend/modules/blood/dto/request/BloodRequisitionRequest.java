package com.ciphertext.opencarebackend.modules.blood.dto.request;

import com.ciphertext.opencarebackend.config.RegionDefaults;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BloodRequisitionRequest {
    @NotNull(message = "Requester ID is required")
    @Positive(message = "Requester ID must be a positive number")
    private Long requesterId;

    @NotBlank(message = "Patient name is required")
    @Size(max = 200, message = "Patient name must be at most 200 characters")
    private String patientName;

    @Positive(message = "Patient age must be positive")
    private Integer patientAge;

    @Pattern(regexp = "^(MALE|FEMALE|OTHER)$", message = "Invalid gender value")
    private String patientGender;

    @NotBlank(message = "Blood group is required")
    @Pattern(
            regexp = "^(A_POSITIVE|A_NEGATIVE|B_POSITIVE|B_NEGATIVE|O_POSITIVE|O_NEGATIVE|AB_POSITIVE|AB_NEGATIVE)$",
            message = "Invalid blood group"
    )
    private String bloodGroup;

    @Pattern(
            regexp = "^(WHOLE_BLOOD|PLASMA|PLATELETS|RED_BLOOD_CELLS)$",
            message = "Invalid blood component"
    )
    private String bloodComponent;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be a positive number")
    private Integer quantityBags;

    @NotNull(message = "Needed by date is required")
    @FutureOrPresent(message = "Needed by date cannot be in the past")
    private LocalDate neededByDate;

    @NotNull(message = "Hospital ID is required")
    @Positive(message = "Hospital ID must be a positive number")
    private Integer hospitalId;

    @Size(max = 200, message = "Contact person must be at most 200 characters")
    private String contactPerson;

    @NotBlank(message = "Contact phone is required")
        @Pattern(regexp = RegionDefaults.INDIAN_PHONE_REGEX, message = RegionDefaults.INDIAN_PHONE_MESSAGE)
    private String contactPhone;

    @Size(max = 1000, message = "Description must be at most 1000 characters")
    private String description;

    @Positive(message = "District ID must be a positive number")
    private Integer districtId;

    @Positive(message = "Upazila ID must be a positive number")
    private Integer upazilaId;

    @DecimalMin(value = "-90.0", message = "Latitude must be at least -90")
    @DecimalMax(value = "90.0", message = "Latitude must be at most 90")
    private BigDecimal lat;

    @DecimalMin(value = "-180.0", message = "Longitude must be at least -180")
    @DecimalMax(value = "180.0", message = "Longitude must be at most 180")
    private BigDecimal lon;

    @Pattern(regexp = "^(ACTIVE|FULFILLED|EXPIRED|CANCELLED)$", message = "Invalid requisition status")
    private String status;

    private LocalDate fulfilledDate;
}