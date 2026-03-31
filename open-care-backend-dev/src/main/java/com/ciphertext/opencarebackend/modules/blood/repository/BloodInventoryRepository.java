package com.ciphertext.opencarebackend.modules.blood.repository;

import com.ciphertext.opencarebackend.entity.BloodInventory;
import com.ciphertext.opencarebackend.enums.BloodComponent;
import com.ciphertext.opencarebackend.enums.BloodGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BloodInventoryRepository extends JpaRepository<BloodInventory, Long> {

    List<BloodInventory> findByBloodBankId(Integer bloodBankId);

    List<BloodInventory> findByBloodBankIdAndAvailableUnitsGreaterThan(Integer bloodBankId, Integer minUnits);

    Optional<BloodInventory> findByBloodBankIdAndBloodGroupAndComponent(
            Integer bloodBankId, BloodGroup bloodGroup, BloodComponent component);

    List<BloodInventory> findByBloodGroupAndAvailableUnitsGreaterThan(BloodGroup bloodGroup, Integer minUnits);
}