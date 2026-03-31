package com.ciphertext.opencarebackend.modules.appointment.dto.filter;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
public class AppointmentFilter {
    private String appointmentNumber;
    private Long patientProfileId;
    private Long doctorId;
    private String appointmentType;
    private LocalDate appointmentDateFrom;
    private LocalDate appointmentDateTo;
    private String status;
    private String paymentStatus;
    private Integer hospitalId;
    private Long doctorWorkplaceId;
}