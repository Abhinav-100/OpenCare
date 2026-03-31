package com.ciphertext.opencarebackend.modules.patient.service;

import com.ciphertext.opencarebackend.modules.auth.dto.ModuleAuthResponse;
import com.ciphertext.opencarebackend.modules.patient.dto.PatientAppointmentBookingRequest;
import com.ciphertext.opencarebackend.modules.patient.dto.PatientAppointmentResponse;
import com.ciphertext.opencarebackend.modules.patient.dto.PatientRegisterRequest;
import com.ciphertext.opencarebackend.modules.patient.dto.PatientReportResponse;
import com.ciphertext.opencarebackend.modules.shared.dto.ModuleOverviewResponse;

import java.util.List;

public interface PatientModuleService {

    ModuleAuthResponse registerPatient(PatientRegisterRequest request);

    PatientAppointmentResponse bookAppointment(PatientAppointmentBookingRequest request, String authenticatedUserId);

    List<PatientReportResponse> getMyReports(String authenticatedUserId);

    ModuleOverviewResponse getOverview();
}