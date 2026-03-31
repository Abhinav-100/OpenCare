package com.ciphertext.opencarebackend.modules.user.service;

import com.ciphertext.opencarebackend.entity.AssociationProfile;
import com.ciphertext.opencarebackend.entity.Profile;
import com.ciphertext.opencarebackend.entity.Association;

import java.time.LocalDate;

public interface AssociationProfileService {
    AssociationProfile createAssociationProfile(Profile profile, Association association, String position, LocalDate startDate, LocalDate endDate);
    void removeAssociationProfile(Long profileId, Integer associationId);
    boolean existsActiveAssociationProfile(Long profileId, Integer associationId);
}