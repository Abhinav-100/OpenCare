package com.ciphertext.opencarebackend.modules.provider.repository;

import com.ciphertext.opencarebackend.entity.Hospital;
import com.ciphertext.opencarebackend.enums.HospitalType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author Sadman
 */
@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Integer>, JpaSpecificationExecutor<Hospital> {
    Optional<Hospital> findByRegistrationCode(String registrationCode);
    boolean existsByRegistrationCode(String registrationCode);

    List<Hospital> findAllByHospitalTypeIn(List<HospitalType> hospitalTypeList);
}