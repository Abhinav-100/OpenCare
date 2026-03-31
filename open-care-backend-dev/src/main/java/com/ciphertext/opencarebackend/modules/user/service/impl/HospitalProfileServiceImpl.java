package com.ciphertext.opencarebackend.modules.user.service.impl;

import com.ciphertext.opencarebackend.entity.Hospital;
import com.ciphertext.opencarebackend.entity.HospitalProfile;
import com.ciphertext.opencarebackend.entity.Profile;
import com.ciphertext.opencarebackend.modules.user.repository.HospitalProfileRepository;
import com.ciphertext.opencarebackend.modules.user.service.HospitalProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class HospitalProfileServiceImpl implements HospitalProfileService {

    private final HospitalProfileRepository hospitalProfileRepository;

    @Override
    public HospitalProfile createHospitalProfile(Profile profile, Hospital hospital, String position, LocalDate startDate, LocalDate endDate) {
        HospitalProfile hospitalProfile = new HospitalProfile();
        hospitalProfile.setProfile(profile);
        hospitalProfile.setHospital(hospital);
        hospitalProfile.setPosition(position);
        hospitalProfile.setStartDate(startDate);
        hospitalProfile.setEndDate(endDate);
        hospitalProfile.setIsActive(true);

        HospitalProfile saved = hospitalProfileRepository.save(hospitalProfile);
        log.info("Created hospital profile with ID {} for profile {} and hospital {}", saved.getId(), profile.getId(), hospital.getId());
        return saved;
    }

    @Override
    public void removeHospitalProfile(Long profileId, Integer hospitalId) {
        hospitalProfileRepository.deleteByProfileIdAndHospitalId(profileId, hospitalId);
        log.info("Removed hospital profile for profile {} and hospital {}", profileId, hospitalId);
    }

    @Override
    public boolean existsActiveHospitalProfile(Long profileId, Integer hospitalId) {
        return hospitalProfileRepository.findByProfileIdAndHospitalIdAndIsActiveTrue(profileId, hospitalId).isPresent();
    }
}
