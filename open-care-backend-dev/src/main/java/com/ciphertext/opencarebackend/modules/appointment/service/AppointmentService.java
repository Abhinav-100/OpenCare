package com.ciphertext.opencarebackend.modules.appointment.service;
import com.ciphertext.opencarebackend.modules.appointment.dto.filter.AppointmentFilter;
import com.ciphertext.opencarebackend.entity.Appointment;
import com.ciphertext.opencarebackend.enums.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentService {

    Page<Appointment> getPaginatedDataWithFilters(AppointmentFilter filter, Pageable pageable);

    List<Appointment> getAllAppointments();

    Appointment getAppointmentById(Long id);

    Appointment getAppointmentByNumber(String appointmentNumber);

    Appointment createAppointment(Appointment appointment, String keycloakUserId);

    Appointment updateAppointment(Appointment appointment, Long id);

    Appointment updateAppointmentStatus(Long id, AppointmentStatus status);

    void cancelAppointment(Long id, String reason, String cancelledBy);

    void deleteAppointmentById(Long id);

    List<Appointment> getMyAppointments(String keycloakUserId);

    List<AvailableSlot> getAvailableSlots(Long doctorId, LocalDate date);

    boolean isSlotAvailable(Long doctorId, LocalDate date, LocalTime startTime, LocalTime endTime);

    record AvailableSlot(LocalTime startTime, LocalTime endTime, boolean isAvailable) {}
}
