package com.ciphertext.opencarebackend.modules.user.service;

import com.ciphertext.opencarebackend.entity.InstitutionProfile;
import com.ciphertext.opencarebackend.entity.Profile;
import com.ciphertext.opencarebackend.entity.Institution;

import java.time.LocalDate;

public interface InstitutionProfileService {
    InstitutionProfile createInstitutionProfile(Profile profile, Institution institution, String position, LocalDate startDate, LocalDate endDate);
    void removeInstitutionProfile(Long profileId, Integer institutionId);
    boolean existsActiveInstitutionProfile(Long profileId, Integer institutionId);
}