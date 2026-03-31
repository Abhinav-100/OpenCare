package com.ciphertext.opencarebackend.modules.lab.service;

import com.ciphertext.opencarebackend.modules.lab.dto.LabReportResponse;
import com.ciphertext.opencarebackend.modules.lab.dto.LabReportUploadRequest;
import com.ciphertext.opencarebackend.modules.shared.dto.ModuleOverviewResponse;

public interface LabModuleService {

    LabReportResponse uploadReport(LabReportUploadRequest request, String authenticatedUserId);

    ModuleOverviewResponse getOverview();
}