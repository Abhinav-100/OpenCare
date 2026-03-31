package com.ciphertext.opencarebackend.modules.clinical.repository;

import com.ciphertext.opencarebackend.entity.HealthCondition;
import com.ciphertext.opencarebackend.enums.ConditionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HealthConditionRepository extends JpaRepository<HealthCondition, Long> {

    List<HealthCondition> findByProfileIdOrderByLastUpdatedDesc(Long profileId);

    List<HealthCondition> findByProfileIdAndConditionType(Long profileId, ConditionType conditionType);

    @Query("SELECT hc FROM HealthCondition hc WHERE hc.profile.keycloakUserId = :keycloakUserId ORDER BY hc.lastUpdated DESC")
    List<HealthCondition> findByKeycloakUserId(@Param("keycloakUserId") String keycloakUserId);
}