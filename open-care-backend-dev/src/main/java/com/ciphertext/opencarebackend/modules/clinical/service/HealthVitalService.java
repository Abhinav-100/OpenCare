package com.ciphertext.opencarebackend.modules.clinical.service;
import com.ciphertext.opencarebackend.modules.clinical.dto.response.LatestHealthVitalProjection;
import com.ciphertext.opencarebackend.entity.HealthVital;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface HealthVitalService {

    HealthVital createHealthVital(HealthVital healthVital);

    HealthVital getHealthVitalById(Long id);

    HealthVital updateHealthVital(Long id, HealthVital healthVital);

    void deleteHealthVital(Long id);

    Page<HealthVital> getAllHealthVitals(Pageable pageable);

    Page<HealthVital> getHealthVitalsByProfileId(Long profileId, Pageable pageable);

    Page<HealthVital> getHealthVitalsByKeycloakUserId(String keycloakUserId, Pageable pageable);

    List<HealthVital> getHealthVitalHistoryByProfileId(Long profileId);

    LatestHealthVitalProjection getLatestHealthVitalByProfileId(Long profileId);

    LatestHealthVitalProjection getLatestHealthVitalByKeycloakUserId(String keycloakUserId);
}
