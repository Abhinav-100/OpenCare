package com.ciphertext.opencarebackend.modules.clinical.repository;

import com.ciphertext.opencarebackend.entity.HealthEncounter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HealthEncounterRepository extends JpaRepository<HealthEncounter, Long> {

    List<HealthEncounter> findByProfileIdOrderByVisitDateDesc(Long profileId);

    @Query("SELECT he FROM HealthEncounter he WHERE he.profile.keycloakUserId = :keycloakUserId ORDER BY he.visitDate DESC")
    List<HealthEncounter> findByKeycloakUserId(@Param("keycloakUserId") String keycloakUserId);
}