package com.ciphertext.opencarebackend.modules.blood.repository;

import com.ciphertext.opencarebackend.entity.BloodRequisition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BloodRequisitionRepository extends JpaRepository<BloodRequisition, Long>, JpaSpecificationExecutor<BloodRequisition> {
}