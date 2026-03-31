package com.ciphertext.opencarebackend.modules.shared.repository;

import com.ciphertext.opencarebackend.modules.shared.entity.HmsDoctorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HmsDoctorRepository extends JpaRepository<HmsDoctorEntity, Long> {

    Optional<HmsDoctorEntity> findByUserId(Long userId);
}