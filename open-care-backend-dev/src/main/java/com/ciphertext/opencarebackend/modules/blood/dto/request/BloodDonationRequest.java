package com.ciphertext.opencarebackend.modules.blood.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class BloodDonationRequest {
    @NotNull(message = "Donor ID is required")
    @Positive(message = "Donor ID must be a positive number")
    private Long donorId;

    @NotNull(message = "Hospital ID is required")
    @Positive(message = "Hospital ID must be a positive number")
    private Integer hospitalId;

    @NotNull(message = "Donation date is required")
    @PastOrPresent(message = "Donation date cannot be in the future")
    private LocalDate donationDate;

    @NotBlank(message = "Blood group is required")
    @Pattern(
            regexp = "^(A_POSITIVE|A_NEGATIVE|B_POSITIVE|B_NEGATIVE|O_POSITIVE|O_NEGATIVE|AB_POSITIVE|AB_NEGATIVE)$",
            message = "Invalid blood group"
    )
    @JsonAlias("bloodType")
    private String bloodGroup;

    @Pattern(
            regexp = "^(WHOLE_BLOOD|PLASMA|PLATELETS|RED_BLOOD_CELLS)$",
            message = "Invalid blood component"
    )
    @JsonAlias("donationType")
    private String bloodComponent;

    @Positive(message = "Quantity must be a positive number")
    private Integer quantityMl;

    private String healthStatus;
    private BigDecimal hemoglobinLevel;
    private String bloodPressure;
    private Long pulseRate;
    private BigDecimal temperature;
    private String notes;
    private String status;
}