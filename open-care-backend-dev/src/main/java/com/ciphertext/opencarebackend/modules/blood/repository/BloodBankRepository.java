package com.ciphertext.opencarebackend.modules.blood.repository;

import com.ciphertext.opencarebackend.entity.BloodBank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BloodBankRepository extends JpaRepository<BloodBank, Integer>, JpaSpecificationExecutor<BloodBank> {

    Optional<BloodBank> findByLicenseNo(String licenseNo);

    List<BloodBank> findByIsActiveTrue();

    List<BloodBank> findByHospitalId(Integer hospitalId);

    List<BloodBank> findByIsAlwaysOpenTrueAndIsActiveTrue();
}