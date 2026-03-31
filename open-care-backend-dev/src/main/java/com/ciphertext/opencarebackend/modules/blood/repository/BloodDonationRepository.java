package com.ciphertext.opencarebackend.modules.blood.repository;

import com.ciphertext.opencarebackend.entity.BloodDonation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface BloodDonationRepository extends JpaRepository<BloodDonation, Long>, JpaSpecificationExecutor<BloodDonation> {
    List<BloodDonation> findAllByDonor_Id(Long donorId);
}