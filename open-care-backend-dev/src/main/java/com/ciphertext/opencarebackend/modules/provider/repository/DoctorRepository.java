package com.ciphertext.opencarebackend.modules.provider.repository;

import com.ciphertext.opencarebackend.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author Sadman
 */
@Repository
public interface DoctorRepository  extends JpaRepository<Doctor, Long>, JpaSpecificationExecutor<Doctor> {
    Optional<Doctor> findByProfileKeycloakUserId(String keycloakUserId);
    Optional<Doctor> findByProfileEmail(String email);
    Optional<Doctor> findByProfileUsername(String username);
    Optional<Doctor> findByBmdcNo(String bmdcNo);
    List<Doctor> findByIsActiveTrueAndIsVerifiedTrue();
    Optional<Doctor> findByIdAndIsActiveTrueAndIsVerifiedTrue(Long id);
    Optional<Doctor> findByProfileEmailAndIsActiveTrueAndIsVerifiedTrue(String email);
    Optional<Doctor> findByProfileUsernameAndIsActiveTrueAndIsVerifiedTrue(String username);
    Optional<Doctor> findByBmdcNoAndIsActiveTrueAndIsVerifiedTrue(String bmdcNo);
    boolean existsByProfileEmail(String email);
    boolean existsByProfileUsername(String username);
    boolean existsByBmdcNo(String bmdcNo);
}