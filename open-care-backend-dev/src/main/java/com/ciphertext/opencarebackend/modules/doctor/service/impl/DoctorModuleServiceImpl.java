package com.ciphertext.opencarebackend.modules.doctor.service.impl;

import com.ciphertext.opencarebackend.exception.BadRequestException;
import com.ciphertext.opencarebackend.exception.ResourceNotFoundException;
import com.ciphertext.opencarebackend.modules.doctor.dto.DoctorAppointmentResponse;
import com.ciphertext.opencarebackend.modules.doctor.dto.DoctorDiagnosisRequest;
import com.ciphertext.opencarebackend.modules.doctor.dto.DoctorLabTestRequest;
import com.ciphertext.opencarebackend.modules.doctor.dto.DoctorLabTestResponse;
import com.ciphertext.opencarebackend.modules.doctor.service.DoctorModuleService;
import com.ciphertext.opencarebackend.modules.shared.entity.HmsAppointmentEntity;
import com.ciphertext.opencarebackend.modules.shared.entity.HmsDoctorEntity;
import com.ciphertext.opencarebackend.modules.shared.entity.HmsLabTestEntity;
import com.ciphertext.opencarebackend.modules.shared.dto.ModuleOverviewResponse;
import com.ciphertext.opencarebackend.modules.shared.repository.HmsAppointmentRepository;
import com.ciphertext.opencarebackend.modules.shared.repository.HmsDoctorRepository;
import com.ciphertext.opencarebackend.modules.shared.repository.HmsLabTestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorModuleServiceImpl implements DoctorModuleService {

    private final HmsDoctorRepository hmsDoctorRepository;
    private final HmsAppointmentRepository hmsAppointmentRepository;
    private final HmsLabTestRepository hmsLabTestRepository;

    @Override
    @Transactional(readOnly = true)
    public List<DoctorAppointmentResponse> getMyAppointments(String authenticatedUserId) {
        HmsDoctorEntity doctorEntity = getDoctorByAuthenticatedUser(authenticatedUserId);
        return hmsAppointmentRepository.findByDoctorIdOrderByScheduledAtDesc(doctorEntity.getId())
                .stream()
                .map(this::toAppointmentResponse)
                .toList();
    }

    @Override
    @Transactional
    public DoctorAppointmentResponse addDiagnosis(Long appointmentId, DoctorDiagnosisRequest request, String authenticatedUserId) {
        HmsDoctorEntity doctorEntity = getDoctorByAuthenticatedUser(authenticatedUserId);
        HmsAppointmentEntity appointmentEntity = hmsAppointmentRepository.findByIdAndDoctorId(appointmentId, doctorEntity.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found for this doctor"));

        appointmentEntity.setDiagnosis(request.diagnosis());
        appointmentEntity.setNotes(request.notes());
        appointmentEntity.setStatus("COMPLETED");
        appointmentEntity.setUpdatedAt(LocalDateTime.now());

        HmsAppointmentEntity saved = hmsAppointmentRepository.save(appointmentEntity);
        return toAppointmentResponse(saved);
    }

    @Override
    @Transactional
    public DoctorLabTestResponse requestLabTest(DoctorLabTestRequest request, String authenticatedUserId) {
        HmsDoctorEntity doctorEntity = getDoctorByAuthenticatedUser(authenticatedUserId);
        HmsAppointmentEntity appointmentEntity = hmsAppointmentRepository.findByIdAndDoctorId(request.appointmentId(), doctorEntity.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found for this doctor"));

        HmsLabTestEntity labTestEntity = new HmsLabTestEntity();
        labTestEntity.setAppointmentId(appointmentEntity.getId());
        labTestEntity.setPatientId(appointmentEntity.getPatientId());
        labTestEntity.setDoctorId(doctorEntity.getId());
        labTestEntity.setTestCode(request.testCode());
        labTestEntity.setTestName(request.testName());
        labTestEntity.setNotes(request.notes());
        labTestEntity.setStatus("REQUESTED");
        labTestEntity.setRequestedAt(LocalDateTime.now());
        labTestEntity.setCreatedAt(LocalDateTime.now());
        labTestEntity.setUpdatedAt(LocalDateTime.now());

        HmsLabTestEntity saved = hmsLabTestRepository.save(labTestEntity);
        return toLabTestResponse(saved);
    }

    @Override
    public ModuleOverviewResponse getOverview() {
        return new ModuleOverviewResponse(
                "doctor",
                "active",
                List.of("doctor-profile", "appointment-management", "diagnosis-entry")
        );
    }

    private HmsDoctorEntity getDoctorByAuthenticatedUser(String authenticatedUserId) {
        Long userId;
        try {
            userId = Long.parseLong(authenticatedUserId);
        } catch (NumberFormatException ex) {
            throw new BadRequestException("Invalid authenticated user context");
        }

        return hmsDoctorRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found for authenticated user"));
    }

    private DoctorAppointmentResponse toAppointmentResponse(HmsAppointmentEntity appointmentEntity) {
        return new DoctorAppointmentResponse(
                appointmentEntity.getId(),
                appointmentEntity.getAppointmentNumber(),
                appointmentEntity.getPatientId(),
                appointmentEntity.getScheduledAt(),
                appointmentEntity.getDurationMinutes(),
                appointmentEntity.getStatus(),
                appointmentEntity.getReason(),
                appointmentEntity.getDiagnosis()
        );
    }

    private DoctorLabTestResponse toLabTestResponse(HmsLabTestEntity labTestEntity) {
        return new DoctorLabTestResponse(
                labTestEntity.getId(),
                labTestEntity.getAppointmentId(),
                labTestEntity.getPatientId(),
                labTestEntity.getDoctorId(),
                labTestEntity.getTestCode(),
                labTestEntity.getTestName(),
                labTestEntity.getStatus(),
                labTestEntity.getRequestedAt()
        );
    }
}