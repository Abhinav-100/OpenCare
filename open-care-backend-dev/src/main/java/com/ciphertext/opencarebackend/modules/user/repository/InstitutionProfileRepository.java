package com.ciphertext.opencarebackend.modules.user.repository;

import com.ciphertext.opencarebackend.entity.InstitutionProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InstitutionProfileRepository extends JpaRepository<InstitutionProfile, Long>, JpaSpecificationExecutor<InstitutionProfile> {
    Optional<InstitutionProfile> findByProfileIdAndInstitutionIdAndIsActiveTrue(Long profileId, Integer institutionId);
    void deleteByProfileIdAndInstitutionId(Long profileId, Integer institutionId);
}