package com.ciphertext.opencarebackend.modules.user.service.impl;

import com.ciphertext.opencarebackend.entity.Institution;
import com.ciphertext.opencarebackend.entity.InstitutionProfile;
import com.ciphertext.opencarebackend.entity.Profile;
import com.ciphertext.opencarebackend.modules.user.repository.InstitutionProfileRepository;
import com.ciphertext.opencarebackend.modules.user.service.InstitutionProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class InstitutionProfileServiceImpl implements InstitutionProfileService {

    private final InstitutionProfileRepository institutionProfileRepository;

    @Override
    public InstitutionProfile createInstitutionProfile(Profile profile, Institution institution, String position, LocalDate startDate, LocalDate endDate) {
        InstitutionProfile institutionProfile = new InstitutionProfile();
        institutionProfile.setProfile(profile);
        institutionProfile.setInstitution(institution);
        institutionProfile.setPosition(position);
        institutionProfile.setStartDate(startDate);
        institutionProfile.setEndDate(endDate);
        institutionProfile.setIsActive(true);

        InstitutionProfile saved = institutionProfileRepository.save(institutionProfile);
        log.info("Created institution profile with ID {} for profile {} and institution {}", saved.getId(), profile.getId(), institution.getId());
        return saved;
    }

    @Override
    public void removeInstitutionProfile(Long profileId, Integer institutionId) {
        institutionProfileRepository.deleteByProfileIdAndInstitutionId(profileId, institutionId);
        log.info("Removed institution profile for profile {} and institution {}", profileId, institutionId);
    }

    @Override
    public boolean existsActiveInstitutionProfile(Long profileId, Integer institutionId) {
        return institutionProfileRepository.findByProfileIdAndInstitutionIdAndIsActiveTrue(profileId, institutionId).isPresent();
    }
}
