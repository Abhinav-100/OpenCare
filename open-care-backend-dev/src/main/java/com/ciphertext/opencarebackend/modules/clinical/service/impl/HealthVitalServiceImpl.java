package com.ciphertext.opencarebackend.modules.clinical.service.impl;
import com.ciphertext.opencarebackend.modules.clinical.dto.response.LatestHealthVitalProjection;
import com.ciphertext.opencarebackend.entity.HealthVital;
import com.ciphertext.opencarebackend.entity.Profile;
import com.ciphertext.opencarebackend.exception.ResourceNotFoundException;
import com.ciphertext.opencarebackend.modules.clinical.repository.HealthVitalRepository;
import com.ciphertext.opencarebackend.modules.user.repository.ProfileRepository;
import com.ciphertext.opencarebackend.modules.clinical.service.HealthVitalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class HealthVitalServiceImpl implements HealthVitalService {

    private final HealthVitalRepository healthVitalRepository;
    private final ProfileRepository profileRepository;

    @Override
    @Transactional
    public HealthVital createHealthVital(HealthVital healthVital) {
        log.info("Creating health vital for profile ID: {}", healthVital.getProfile().getId());
        return healthVitalRepository.save(healthVital);
    }

    @Override
    @Transactional(readOnly = true)
    public HealthVital getHealthVitalById(Long id) {
        log.info("Fetching health vital with ID: {}", id);
        return healthVitalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Health Vital not found with id: " + id));
    }

    @Override
    @Transactional
    public HealthVital updateHealthVital(Long id, HealthVital healthVital) {
        log.info("Updating health vital with ID: {}", id);
        HealthVital existingHealthVital = healthVitalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Health Vital not found with id: " + id));

        healthVital.setId(id);
        healthVital.setProfile(existingHealthVital.getProfile());
        return healthVitalRepository.save(healthVital);
    }

    @Override
    @Transactional
    public void deleteHealthVital(Long id) {
        log.info("Deleting health vital with ID: {}", id);
        HealthVital existingHealthVital = healthVitalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Health Vital not found with id: " + id));
        healthVitalRepository.delete(existingHealthVital);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HealthVital> getAllHealthVitals(Pageable pageable) {
        log.info("Fetching all health vitals with pagination");
        return healthVitalRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HealthVital> getHealthVitalsByProfileId(Long profileId, Pageable pageable) {
        log.info("Fetching health vitals for profile ID: {}", profileId);
        return healthVitalRepository.findByProfileId(profileId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HealthVital> getHealthVitalsByKeycloakUserId(String keycloakUserId, Pageable pageable) {
        log.info("Fetching health vitals for Keycloak user ID: {}", keycloakUserId);
        return healthVitalRepository.findByProfileKeycloakUserId(keycloakUserId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HealthVital> getHealthVitalHistoryByProfileId(Long profileId) {
        log.info("Fetching complete health vital history for profile ID: {}", profileId);
        return healthVitalRepository.findByProfileIdOrderByRecordedAtDesc(profileId);
    }

    @Override
    @Transactional(readOnly = true)
    public LatestHealthVitalProjection getLatestHealthVitalByProfileId(Long profileId) {
        log.info("Fetching latest health vital for profile ID: {}", profileId);
        return healthVitalRepository.findLatestByProfileId(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("No health vitals found for profile id: " + profileId));
    }

    @Override
    @Transactional(readOnly = true)
    public LatestHealthVitalProjection getLatestHealthVitalByKeycloakUserId(String keycloakUserId) {
        log.info("Fetching latest health vital for Keycloak user ID: {}", keycloakUserId);
        Optional<Profile> profile = profileRepository.findByKeycloakUserId(keycloakUserId);
        if (profile.isEmpty()) {
            throw new ResourceNotFoundException("Profile not found for Keycloak user id: " + keycloakUserId);
        }
        Long profileId = profile.get().getId();
        return healthVitalRepository.findLatestByProfileId(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("No health vitals found for profile id: " + profileId));
    }
}
