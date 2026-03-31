package com.ciphertext.opencarebackend.modules.doctor.service;

import com.ciphertext.opencarebackend.modules.doctor.dto.DoctorAppointmentResponse;
import com.ciphertext.opencarebackend.modules.doctor.dto.DoctorDiagnosisRequest;
import com.ciphertext.opencarebackend.modules.doctor.dto.DoctorLabTestRequest;
import com.ciphertext.opencarebackend.modules.doctor.dto.DoctorLabTestResponse;
import com.ciphertext.opencarebackend.modules.shared.dto.ModuleOverviewResponse;

import java.util.List;

public interface DoctorModuleService {

    List<DoctorAppointmentResponse> getMyAppointments(String authenticatedUserId);

    DoctorAppointmentResponse addDiagnosis(Long appointmentId, DoctorDiagnosisRequest request, String authenticatedUserId);

    DoctorLabTestResponse requestLabTest(DoctorLabTestRequest request, String authenticatedUserId);

    ModuleOverviewResponse getOverview();
}