package com.ciphertext.opencarebackend.modules.user.service;

import com.ciphertext.opencarebackend.entity.SocialOrganizationProfile;
import com.ciphertext.opencarebackend.entity.Profile;
import com.ciphertext.opencarebackend.entity.SocialOrganization;

import java.time.LocalDate;

public interface SocialOrganizationProfileService {
    SocialOrganizationProfile createSocialOrganizationProfile(Profile profile, SocialOrganization socialOrganization, String position, LocalDate startDate, LocalDate endDate);
    void removeSocialOrganizationProfile(Long profileId, Integer socialOrganizationId);
    boolean existsActiveSocialOrganizationProfile(Long profileId, Integer socialOrganizationId);
}