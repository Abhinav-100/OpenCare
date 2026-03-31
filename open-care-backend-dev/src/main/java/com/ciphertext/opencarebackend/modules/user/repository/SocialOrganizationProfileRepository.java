package com.ciphertext.opencarebackend.modules.user.repository;

import com.ciphertext.opencarebackend.entity.SocialOrganizationProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SocialOrganizationProfileRepository extends JpaRepository<SocialOrganizationProfile, Long>, JpaSpecificationExecutor<SocialOrganizationProfile> {
    Optional<SocialOrganizationProfile> findByProfileIdAndSocialOrganizationIdAndIsActiveTrue(Long profileId, Integer socialOrganizationId);
    void deleteByProfileIdAndSocialOrganizationId(Long profileId, Integer socialOrganizationId);
}