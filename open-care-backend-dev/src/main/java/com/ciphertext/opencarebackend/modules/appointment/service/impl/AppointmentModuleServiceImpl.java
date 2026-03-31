package com.ciphertext.opencarebackend.modules.appointment.service.impl;
import com.ciphertext.opencarebackend.modules.appointment.dto.request.AppointmentRequest;
import com.ciphertext.opencarebackend.modules.appointment.dto.response.AppointmentResponse;
import com.ciphertext.opencarebackend.entity.Appointment;
import com.ciphertext.opencarebackend.enums.AppointmentStatus;
import com.ciphertext.opencarebackend.mapper.AppointmentMapper;
import com.ciphertext.opencarebackend.modules.appointment.service.AppointmentModuleService;
import com.ciphertext.opencarebackend.modules.appointment.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentModuleServiceImpl implements AppointmentModuleService {

    private final AppointmentService appointmentService;
    private final AppointmentMapper appointmentMapper;

    @Override
    public AppointmentResponse createAppointment(AppointmentRequest request, String keycloakUserId) {
        Appointment appointment = appointmentMapper.toEntity(request);
        Appointment savedAppointment = appointmentService.createAppointment(appointment, keycloakUserId);
        return appointmentMapper.toResponse(savedAppointment);
    }

    @Override
    public List<AppointmentResponse> getMyAppointments(String keycloakUserId) {
        return appointmentService.getMyAppointments(keycloakUserId)
                .stream()
                .map(appointmentMapper::toResponse)
                .toList();
    }

    @Override
    public AppointmentResponse updateAppointmentStatus(Long appointmentId, AppointmentStatus status) {
        Appointment updatedAppointment = appointmentService.updateAppointmentStatus(appointmentId, status);
        return appointmentMapper.toResponse(updatedAppointment);
    }
}
