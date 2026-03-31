package com.ciphertext.opencarebackend.modules.patient.service.impl;

import com.ciphertext.opencarebackend.exception.BadRequestException;
import com.ciphertext.opencarebackend.exception.DuplicateResourceException;
import com.ciphertext.opencarebackend.exception.ResourceNotFoundException;
import com.ciphertext.opencarebackend.modules.auth.dto.ModuleAuthResponse;
import com.ciphertext.opencarebackend.modules.auth.entity.AuthRoleEntity;
import com.ciphertext.opencarebackend.modules.auth.entity.AuthUserEntity;
import com.ciphertext.opencarebackend.modules.auth.repository.AuthRoleRepository;
import com.ciphertext.opencarebackend.modules.auth.repository.AuthUserRepository;
import com.ciphertext.opencarebackend.modules.auth.service.ModuleJwtService;
import com.ciphertext.opencarebackend.modules.patient.dto.PatientAppointmentBookingRequest;
import com.ciphertext.opencarebackend.modules.patient.dto.PatientAppointmentResponse;
import com.ciphertext.opencarebackend.modules.patient.dto.PatientRegisterRequest;
import com.ciphertext.opencarebackend.modules.patient.dto.PatientReportResponse;
import com.ciphertext.opencarebackend.modules.patient.service.PatientModuleService;
import com.ciphertext.opencarebackend.modules.shared.entity.HmsAppointmentEntity;
import com.ciphertext.opencarebackend.modules.shared.entity.HmsDoctorEntity;
import com.ciphertext.opencarebackend.modules.shared.entity.HmsPatientEntity;
import com.ciphertext.opencarebackend.modules.shared.entity.HmsReportEntity;
import com.ciphertext.opencarebackend.modules.shared.dto.ModuleOverviewResponse;
import com.ciphertext.opencarebackend.modules.shared.repository.HmsAppointmentRepository;
import com.ciphertext.opencarebackend.modules.shared.repository.HmsDoctorRepository;
import com.ciphertext.opencarebackend.modules.shared.repository.HmsPatientRepository;
import com.ciphertext.opencarebackend.modules.shared.repository.HmsReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PatientModuleServiceImpl implements PatientModuleService {

    private final AuthUserRepository authUserRepository;
    private final AuthRoleRepository authRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModuleJwtService moduleJwtService;
    private final HmsPatientRepository hmsPatientRepository;
    private final HmsDoctorRepository hmsDoctorRepository;
    private final HmsAppointmentRepository hmsAppointmentRepository;
    private final HmsReportRepository hmsReportRepository;

    @Override
    @Transactional
    public ModuleAuthResponse registerPatient(PatientRegisterRequest request) {
        if (authUserRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("User already exists with this email");
        }

        AuthRoleEntity patientRole = authRoleRepository.findByCode("PATIENT")
                .orElseThrow(() -> new BadRequestException("PATIENT role is missing"));

        AuthUserEntity userEntity = new AuthUserEntity();
        userEntity.setEmail(request.email().toLowerCase());
        userEntity.setPasswordHash(passwordEncoder.encode(request.password()));
        userEntity.setFirstName(request.firstName());
        userEntity.setLastName(request.lastName());
        userEntity.setPhone(request.phone());
        userEntity.setStatus("ACTIVE");
        userEntity.setCreatedAt(LocalDateTime.now());
        userEntity.setUpdatedAt(LocalDateTime.now());
        userEntity.setRoles(Set.of(patientRole));

        AuthUserEntity savedUser = authUserRepository.save(userEntity);

        HmsPatientEntity patientEntity = new HmsPatientEntity();
        patientEntity.setUserId(savedUser.getId());
        patientEntity.setMrn("MRN-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase());
        patientEntity.setDateOfBirth(request.dateOfBirth());
        patientEntity.setGender(request.gender().toUpperCase());
        patientEntity.setBloodGroup(request.bloodGroup());
        patientEntity.setAddress(request.address());
        patientEntity.setEmergencyContactName(request.emergencyContactName());
        patientEntity.setEmergencyContactPhone(request.emergencyContactPhone());
        patientEntity.setCreatedAt(LocalDateTime.now());
        patientEntity.setUpdatedAt(LocalDateTime.now());
        hmsPatientRepository.save(patientEntity);

        String token = moduleJwtService.generateToken(savedUser);
        return new ModuleAuthResponse(
                token,
                "Bearer",
                moduleJwtService.getExpirationSeconds(),
                savedUser.getEmail(),
                List.of("PATIENT")
        );
    }

    @Override
    @Transactional
    public PatientAppointmentResponse bookAppointment(PatientAppointmentBookingRequest request, String authenticatedUserId) {
        Long userId = parseUserId(authenticatedUserId);
        HmsPatientEntity patientEntity = hmsPatientRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found for authenticated user"));

        HmsDoctorEntity doctorEntity = hmsDoctorRepository.findById(request.doctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found: " + request.doctorId()));

        HmsAppointmentEntity appointmentEntity = new HmsAppointmentEntity();
        appointmentEntity.setAppointmentNumber("APT-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase());
        appointmentEntity.setPatientId(patientEntity.getId());
        appointmentEntity.setDoctorId(doctorEntity.getId());
        appointmentEntity.setScheduledAt(request.scheduledAt());
        appointmentEntity.setDurationMinutes(request.durationMinutes());
        appointmentEntity.setStatus("PENDING");
        appointmentEntity.setReason(request.reason());
        appointmentEntity.setCreatedAt(LocalDateTime.now());
        appointmentEntity.setUpdatedAt(LocalDateTime.now());

        HmsAppointmentEntity saved = hmsAppointmentRepository.save(appointmentEntity);
        return toAppointmentResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientReportResponse> getMyReports(String authenticatedUserId) {
        Long userId = parseUserId(authenticatedUserId);
        HmsPatientEntity patientEntity = hmsPatientRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found for authenticated user"));

        return hmsReportRepository.findByPatientIdOrderByReportedAtDesc(patientEntity.getId())
                .stream()
                .map(this::toReportResponse)
                .toList();
    }

    @Override
    public ModuleOverviewResponse getOverview() {
        return new ModuleOverviewResponse(
                "patient",
                "active",
                List.of("patient-profile", "appointment-booking", "report-view")
        );
    }

    private Long parseUserId(String authenticatedUserId) {
        try {
            return Long.parseLong(authenticatedUserId);
        } catch (NumberFormatException ex) {
            throw new BadRequestException("Invalid authenticated user context");
        }
    }

    private PatientAppointmentResponse toAppointmentResponse(HmsAppointmentEntity appointment) {
        return new PatientAppointmentResponse(
                appointment.getId(),
                appointment.getAppointmentNumber(),
                appointment.getDoctorId(),
                appointment.getScheduledAt(),
                appointment.getDurationMinutes(),
                appointment.getStatus(),
                appointment.getReason(),
                appointment.getDiagnosis()
        );
    }

    private PatientReportResponse toReportResponse(HmsReportEntity reportEntity) {
        return new PatientReportResponse(
                reportEntity.getId(),
                reportEntity.getLabTestId(),
                reportEntity.getReportType(),
                reportEntity.getFileUrl(),
                reportEntity.getSummary(),
                reportEntity.getStatus(),
                reportEntity.getReportedAt()
        );
    }
}