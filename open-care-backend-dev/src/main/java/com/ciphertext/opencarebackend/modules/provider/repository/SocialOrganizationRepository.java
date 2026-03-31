package com.ciphertext.opencarebackend.modules.provider.repository;

import com.ciphertext.opencarebackend.entity.SocialOrganization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Sadman
 */
@Repository
public interface SocialOrganizationRepository extends JpaRepository<SocialOrganization, Integer>, JpaSpecificationExecutor<SocialOrganization> {

    @Query("SELECT so FROM SocialOrganization so WHERE LOWER(so.tags) LIKE LOWER(CONCAT('%', :tag, '%'))")
    List<SocialOrganization> findByTagsContainingIgnoreCase(@Param("tag") String tag);

    List<SocialOrganization> findByNameContainingIgnoreCase(String name);

    @Query("SELECT so FROM SocialOrganization so WHERE so.phone LIKE %:phone%")
    List<SocialOrganization> findByPhoneContaining(@Param("phone") String phone);

    List<SocialOrganization> findByEmailContainingIgnoreCase(String email);
}