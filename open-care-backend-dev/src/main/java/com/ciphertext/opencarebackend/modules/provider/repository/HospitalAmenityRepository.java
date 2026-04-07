package com.ciphertext.opencarebackend.modules.provider.repository;

import com.ciphertext.opencarebackend.entity.HospitalAmenity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author Sadman
 */
@Repository
/**
 * Flow note: HospitalAmenityRepository belongs to the provider doctor/hospital module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public interface HospitalAmenityRepository extends JpaRepository<HospitalAmenity, Long>, JpaSpecificationExecutor<HospitalAmenity> {

    List<HospitalAmenity> findByHospitalId(Long hospitalId);
    Optional<HospitalAmenity> findByIdAndHospitalId(Long id, Long hospitalId);
    long countAllByHospital_Id(int hospitalId);
}