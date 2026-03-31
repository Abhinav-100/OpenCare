package com.ciphertext.opencarebackend.modules.user.service.impl;

import com.ciphertext.opencarebackend.entity.Association;
import com.ciphertext.opencarebackend.entity.AssociationProfile;
import com.ciphertext.opencarebackend.entity.Profile;
import com.ciphertext.opencarebackend.modules.user.repository.AssociationProfileRepository;
import com.ciphertext.opencarebackend.modules.user.service.AssociationProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class AssociationProfileServiceImpl implements AssociationProfileService {

    private final AssociationProfileRepository associationProfileRepository;

    @Override
    public AssociationProfile createAssociationProfile(Profile profile, Association association, String position, LocalDate startDate, LocalDate endDate) {
        AssociationProfile associationProfile = new AssociationProfile();
        associationProfile.setProfile(profile);
        associationProfile.setAssociation(association);
        associationProfile.setPosition(position);
        associationProfile.setStartDate(startDate);
        associationProfile.setEndDate(endDate);
        associationProfile.setIsActive(true);

        AssociationProfile saved = associationProfileRepository.save(associationProfile);
        log.info("Created association profile with ID {} for profile {} and association {}", saved.getId(), profile.getId(), association.getId());
        return saved;
    }

    @Override
    public void removeAssociationProfile(Long profileId, Integer associationId) {
        associationProfileRepository.deleteByProfileIdAndAssociationId(profileId, associationId);
        log.info("Removed association profile for profile {} and association {}", profileId, associationId);
    }

    @Override
    public boolean existsActiveAssociationProfile(Long profileId, Integer associationId) {
        return associationProfileRepository.findByProfileIdAndAssociationIdAndIsActiveTrue(profileId, associationId).isPresent();
    }
}
