package com.ciphertext.opencarebackend.modules.shared.repository;

import com.ciphertext.opencarebackend.modules.shared.entity.HmsReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HmsReportRepository extends JpaRepository<HmsReportEntity, Long> {

    List<HmsReportEntity> findByPatientIdOrderByReportedAtDesc(Long patientId);
}