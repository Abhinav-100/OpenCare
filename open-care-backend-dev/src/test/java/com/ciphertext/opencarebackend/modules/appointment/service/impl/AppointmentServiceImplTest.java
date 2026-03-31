package com.ciphertext.opencarebackend.modules.appointment.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ciphertext.opencarebackend.entity.Appointment;
import com.ciphertext.opencarebackend.entity.Doctor;
import com.ciphertext.opencarebackend.entity.Profile;
import com.ciphertext.opencarebackend.enums.AppointmentType;
import com.ciphertext.opencarebackend.enums.UserType;
import com.ciphertext.opencarebackend.exception.BadRequestException;
import com.ciphertext.opencarebackend.modules.appointment.repository.AppointmentRepository;
import com.ciphertext.opencarebackend.modules.provider.repository.DoctorRepository;
import com.ciphertext.opencarebackend.modules.provider.repository.DoctorScheduleRepository;
import com.ciphertext.opencarebackend.modules.provider.repository.DoctorWorkplaceRepository;
import com.ciphertext.opencarebackend.modules.provider.repository.HospitalRepository;
import com.ciphertext.opencarebackend.modules.user.repository.ProfileRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private HospitalRepository hospitalRepository;

    @Mock
    private DoctorWorkplaceRepository doctorWorkplaceRepository;

    @Mock
    private DoctorScheduleRepository doctorScheduleRepository;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    @Test
    void createAppointmentRejectsInactiveDoctor() {
        Profile patientProfile = new Profile();
        patientProfile.setId(10L);
        patientProfile.setUserType(UserType.USER);

        when(profileRepository.findByKeycloakUserId("patient-kc-id")).thenReturn(Optional.of(patientProfile));

        Doctor doctorRef = new Doctor();
        doctorRef.setId(99L);

        Doctor inactiveDoctor = new Doctor();
        inactiveDoctor.setId(99L);
        inactiveDoctor.setIsActive(false);
        inactiveDoctor.setIsVerified(true);

        when(doctorRepository.findById(99L)).thenReturn(Optional.of(inactiveDoctor));

        Appointment request = new Appointment();
        request.setDoctor(doctorRef);
        request.setAppointmentType(AppointmentType.ONLINE);
        request.setAppointmentDate(LocalDate.now().plusDays(1));
        request.setStartTime(LocalTime.of(10, 0));
        request.setConsultationFee(BigDecimal.valueOf(500));

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> appointmentService.createAppointment(request, "patient-kc-id"));

        assertEquals("Doctor is not active for bookings", exception.getMessage());
        verify(appointmentRepository, never()).save(any());
    }
}
