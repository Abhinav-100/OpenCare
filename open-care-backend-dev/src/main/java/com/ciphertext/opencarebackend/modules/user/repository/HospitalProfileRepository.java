package com.ciphertext.opencarebackend.modules.user.repository;

import com.ciphertext.opencarebackend.entity.HospitalProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HospitalProfileRepository extends JpaRepository<HospitalProfile, Long>, JpaSpecificationExecutor<HospitalProfile> {
    Optional<HospitalProfile> findByProfileIdAndHospitalIdAndIsActiveTrue(Long profileId, Integer hospitalId);
    void deleteByProfileIdAndHospitalId(Long profileId, Integer hospitalId);
}