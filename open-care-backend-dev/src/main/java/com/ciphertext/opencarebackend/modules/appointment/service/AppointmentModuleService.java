package com.ciphertext.opencarebackend.modules.appointment.service;
import com.ciphertext.opencarebackend.modules.appointment.dto.request.AppointmentRequest;
import com.ciphertext.opencarebackend.modules.appointment.dto.response.AppointmentResponse;
import com.ciphertext.opencarebackend.enums.AppointmentStatus;

import java.util.List;

public interface AppointmentModuleService {

    AppointmentResponse createAppointment(AppointmentRequest request, String keycloakUserId);

    List<AppointmentResponse> getMyAppointments(String keycloakUserId);

    AppointmentResponse updateAppointmentStatus(Long appointmentId, AppointmentStatus status);
}
