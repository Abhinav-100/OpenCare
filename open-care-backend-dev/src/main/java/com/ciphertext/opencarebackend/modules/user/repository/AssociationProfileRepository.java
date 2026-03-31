package com.ciphertext.opencarebackend.modules.user.repository;

import com.ciphertext.opencarebackend.entity.AssociationProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssociationProfileRepository extends JpaRepository<AssociationProfile, Long>, JpaSpecificationExecutor<AssociationProfile> {
    Optional<AssociationProfile> findByProfileIdAndAssociationIdAndIsActiveTrue(Long profileId, Integer associationId);
    void deleteByProfileIdAndAssociationId(Long profileId, Integer associationId);
}