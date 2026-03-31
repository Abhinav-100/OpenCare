package com.ciphertext.opencarebackend.modules.lab.service.impl;

import com.ciphertext.opencarebackend.exception.BadRequestException;
import com.ciphertext.opencarebackend.exception.ResourceNotFoundException;
import com.ciphertext.opencarebackend.modules.lab.dto.LabReportResponse;
import com.ciphertext.opencarebackend.modules.lab.dto.LabReportUploadRequest;
import com.ciphertext.opencarebackend.modules.lab.service.LabModuleService;
import com.ciphertext.opencarebackend.modules.shared.entity.HmsLabTestEntity;
import com.ciphertext.opencarebackend.modules.shared.entity.HmsReportEntity;
import com.ciphertext.opencarebackend.modules.shared.dto.ModuleOverviewResponse;
import com.ciphertext.opencarebackend.modules.shared.repository.HmsLabTestRepository;
import com.ciphertext.opencarebackend.modules.shared.repository.HmsReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LabModuleServiceImpl implements LabModuleService {

    private final HmsLabTestRepository hmsLabTestRepository;
    private final HmsReportRepository hmsReportRepository;

    @Override
    @Transactional
    public LabReportResponse uploadReport(LabReportUploadRequest request, String authenticatedUserId) {
        Long uploadedByUserId;
        try {
            uploadedByUserId = Long.parseLong(authenticatedUserId);
        } catch (NumberFormatException ex) {
            throw new BadRequestException("Invalid authenticated user context");
        }

        HmsLabTestEntity labTestEntity = hmsLabTestRepository.findById(request.labTestId())
                .orElseThrow(() -> new ResourceNotFoundException("Lab test not found: " + request.labTestId()));

        HmsReportEntity reportEntity = new HmsReportEntity();
        reportEntity.setLabTestId(labTestEntity.getId());
        reportEntity.setPatientId(labTestEntity.getPatientId());
        reportEntity.setUploadedByUserId(uploadedByUserId);
        reportEntity.setReportType(request.reportType());
        reportEntity.setFileUrl(request.fileUrl());
        reportEntity.setSummary(request.summary());
        reportEntity.setStatus("AVAILABLE");
        reportEntity.setReportedAt(LocalDateTime.now());
        reportEntity.setCreatedAt(LocalDateTime.now());
        reportEntity.setUpdatedAt(LocalDateTime.now());
        HmsReportEntity savedReport = hmsReportRepository.save(reportEntity);

        labTestEntity.setStatus("COMPLETED");
        labTestEntity.setCompletedAt(LocalDateTime.now());
        labTestEntity.setUpdatedAt(LocalDateTime.now());
        hmsLabTestRepository.save(labTestEntity);

        return new LabReportResponse(
                savedReport.getId(),
                savedReport.getLabTestId(),
                savedReport.getPatientId(),
                savedReport.getReportType(),
                savedReport.getFileUrl(),
                savedReport.getStatus(),
                savedReport.getReportedAt()
        );
    }

    @Override
    public ModuleOverviewResponse getOverview() {
        return new ModuleOverviewResponse(
                "lab",
                "active",
                List.of("lab-test-request", "sample-processing", "report-upload")
        );
    }
}