package com.ciphertext.opencarebackend.modules.clinical.repository;
import com.ciphertext.opencarebackend.modules.clinical.dto.response.LatestHealthVitalProjection;
import com.ciphertext.opencarebackend.entity.HealthVital;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HealthVitalRepository extends JpaRepository<HealthVital, Long>, JpaSpecificationExecutor<HealthVital> {

    Page<HealthVital> findByProfileId(Long profileId, Pageable pageable);

    List<HealthVital> findByProfileIdOrderByRecordedAtDesc(Long profileId);

    @Query(value = "SELECT * FROM opencare.v_latest_health_vitals WHERE profile_id = :profileId", nativeQuery = true)
    Optional<LatestHealthVitalProjection> findLatestByProfileId(@Param("profileId") Long profileId);

    @Query("SELECT hv FROM HealthVital hv WHERE hv.profile.keycloakUserId = :keycloakUserId ORDER BY hv.recordedAt DESC")
    Page<HealthVital> findByProfileKeycloakUserId(@Param("keycloakUserId") String keycloakUserId, Pageable pageable);
}
