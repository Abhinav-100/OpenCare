package com.ciphertext.opencarebackend.modules.clinical.repository;

import com.ciphertext.opencarebackend.entity.HealthMedication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HealthMedicationRepository extends JpaRepository<HealthMedication, Long> {

    List<HealthMedication> findByProfileIdOrderByCreatedAtDesc(Long profileId);

    List<HealthMedication> findByProfileIdAndIsActiveTrue(Long profileId);

    @Query("SELECT hm FROM HealthMedication hm WHERE hm.profile.keycloakUserId = :keycloakUserId ORDER BY hm.createdAt DESC")
    List<HealthMedication> findByKeycloakUserId(@Param("keycloakUserId") String keycloakUserId);

    @Query("SELECT hm FROM HealthMedication hm WHERE hm.profile.keycloakUserId = :keycloakUserId AND hm.isActive = true")
    List<HealthMedication> findActiveByKeycloakUserId(@Param("keycloakUserId") String keycloakUserId);
}