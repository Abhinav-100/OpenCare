package com.ciphertext.opencarebackend.modules.user.service.impl;

import com.ciphertext.opencarebackend.entity.Profile;
import com.ciphertext.opencarebackend.entity.SocialOrganization;
import com.ciphertext.opencarebackend.entity.SocialOrganizationProfile;
import com.ciphertext.opencarebackend.modules.user.repository.SocialOrganizationProfileRepository;
import com.ciphertext.opencarebackend.modules.user.service.SocialOrganizationProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class SocialOrganizationProfileServiceImpl implements SocialOrganizationProfileService {

    private final SocialOrganizationProfileRepository socialOrganizationProfileRepository;

    @Override
    public SocialOrganizationProfile createSocialOrganizationProfile(Profile profile, SocialOrganization socialOrganization, String position, LocalDate startDate, LocalDate endDate) {
        SocialOrganizationProfile socialOrganizationProfile = new SocialOrganizationProfile();
        socialOrganizationProfile.setProfile(profile);
        socialOrganizationProfile.setSocialOrganization(socialOrganization);
        socialOrganizationProfile.setPosition(position);
        socialOrganizationProfile.setStartDate(startDate);
        socialOrganizationProfile.setEndDate(endDate);
        socialOrganizationProfile.setIsActive(true);

        SocialOrganizationProfile saved = socialOrganizationProfileRepository.save(socialOrganizationProfile);
        log.info("Created social organization profile with ID {} for profile {} and organization {}", saved.getId(), profile.getId(), socialOrganization.getId());
        return saved;
    }

    @Override
    public void removeSocialOrganizationProfile(Long profileId, Integer socialOrganizationId) {
        socialOrganizationProfileRepository.deleteByProfileIdAndSocialOrganizationId(profileId, socialOrganizationId);
        log.info("Removed social organization profile for profile {} and organization {}", profileId, socialOrganizationId);
    }

    @Override
    public boolean existsActiveSocialOrganizationProfile(Long profileId, Integer socialOrganizationId) {
        return socialOrganizationProfileRepository.findByProfileIdAndSocialOrganizationIdAndIsActiveTrue(profileId, socialOrganizationId).isPresent();
    }
}
