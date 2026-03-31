package com.ciphertext.opencarebackend.modules.user.service;

import com.ciphertext.opencarebackend.entity.HospitalProfile;
import com.ciphertext.opencarebackend.entity.Profile;
import com.ciphertext.opencarebackend.entity.Hospital;

import java.time.LocalDate;

public interface HospitalProfileService {
    HospitalProfile createHospitalProfile(Profile profile, Hospital hospital, String position, LocalDate startDate, LocalDate endDate);
    void removeHospitalProfile(Long profileId, Integer hospitalId);
    boolean existsActiveHospitalProfile(Long profileId, Integer hospitalId);
}