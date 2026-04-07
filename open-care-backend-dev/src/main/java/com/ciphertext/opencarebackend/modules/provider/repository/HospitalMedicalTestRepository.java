package com.ciphertext.opencarebackend.modules.provider.repository;

import com.ciphertext.opencarebackend.entity.HospitalMedicalTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
/**
 * Flow note: HospitalMedicalTestRepository belongs to the provider doctor/hospital module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public interface HospitalMedicalTestRepository extends JpaRepository<HospitalMedicalTest, Long> , JpaSpecificationExecutor<HospitalMedicalTest> {
    List<HospitalMedicalTest> findByHospitalId(Long hospitalId);
    Optional<HospitalMedicalTest> findByIdAndHospitalId(Long id, Long hospitalId);
    long countAllByHospital_Id(int hospitalId);
}