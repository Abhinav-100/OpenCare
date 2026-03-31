package com.ciphertext.opencarebackend.modules.shared.repository;

import com.ciphertext.opencarebackend.modules.shared.entity.HmsLabTestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HmsLabTestRepository extends JpaRepository<HmsLabTestEntity, Long> {
}