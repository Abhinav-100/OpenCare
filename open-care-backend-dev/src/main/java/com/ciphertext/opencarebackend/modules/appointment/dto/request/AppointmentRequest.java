package com.ciphertext.opencarebackend.modules.appointment.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class AppointmentRequest {

    @NotNull(message = "Doctor ID is required")
    @Positive(message = "Doctor ID must be positive")
    private Long doctorId;

    @NotBlank(message = "Appointment type is required")
    @Pattern(regexp = "^(ONLINE|OFFLINE)$", message = "Appointment type must be ONLINE or OFFLINE")
    private String appointmentType;

    @NotNull(message = "Appointment date is required")
    @FutureOrPresent(message = "Appointment date must be today or in the future")
    private LocalDate appointmentDate;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    private LocalTime endTime;

    @Min(value = 15, message = "Duration must be at least 15 minutes")
    @Max(value = 120, message = "Duration cannot exceed 120 minutes")
    private Integer durationMinutes = 30;

    @NotNull(message = "Consultation fee is required")
    @DecimalMin(value = "0.0", message = "Consultation fee cannot be negative")
    private BigDecimal consultationFee;

    @Positive(message = "Hospital ID must be positive")
    private Integer hospitalId;

    @Positive(message = "Doctor workplace ID must be positive")
    private Long doctorWorkplaceId;

    @Size(max = 500, message = "Meeting link must be at most 500 characters")
    private String meetingLink;

    @Size(max = 2000, message = "Symptoms description must be at most 2000 characters")
    private String symptoms;

    @Size(max = 2000, message = "Notes must be at most 2000 characters")
    private String notes;
}