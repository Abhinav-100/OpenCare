package com.ciphertext.opencarebackend.modules.appointment.dto.response;
import com.ciphertext.opencarebackend.modules.appointment.dto.response.enums.AppointmentStatusResponse;
import com.ciphertext.opencarebackend.modules.appointment.dto.response.enums.AppointmentTypeResponse;
import com.ciphertext.opencarebackend.modules.payment.dto.response.enums.PaymentStatusResponse;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import com.ciphertext.opencarebackend.modules.provider.dto.response.DoctorResponse;
import com.ciphertext.opencarebackend.modules.provider.dto.response.DoctorWorkplaceResponse;
import com.ciphertext.opencarebackend.modules.provider.dto.response.HospitalResponse;
import com.ciphertext.opencarebackend.modules.user.dto.response.ProfileResponse;

@Getter
@Setter
public class AppointmentResponse {
    private Long id;
    private String appointmentNumber;
    private ProfileResponse patientProfile;
    private DoctorResponse doctor;
    private AppointmentTypeResponse appointmentType;
    private LocalDate appointmentDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer durationMinutes;
    private AppointmentStatusResponse status;
    private BigDecimal consultationFee;
    private PaymentStatusResponse paymentStatus;
    private String paymentTransactionId;
    private HospitalResponse hospital;
    private DoctorWorkplaceResponse doctorWorkplace;
    private String meetingLink;
    private String symptoms;
    private String notes;
    private String cancellationReason;
    private String cancelledBy;
    private LocalDateTime cancelledAt;
    private Boolean reminderSent;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}