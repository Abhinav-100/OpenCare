package com.ciphertext.opencarebackend.modules.shared.repository;

import com.ciphertext.opencarebackend.modules.shared.entity.HmsPatientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HmsPatientRepository extends JpaRepository<HmsPatientEntity, Long> {

    Optional<HmsPatientEntity> findByUserId(Long userId);
}