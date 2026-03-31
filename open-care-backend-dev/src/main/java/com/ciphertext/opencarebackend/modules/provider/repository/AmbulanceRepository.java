package com.ciphertext.opencarebackend.modules.provider.repository;

import com.ciphertext.opencarebackend.entity.Ambulance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AmbulanceRepository extends JpaRepository<Ambulance, Integer>, JpaSpecificationExecutor<Ambulance> {
    boolean existsByVehicleNumber(String vehicleNumber);

    boolean existsByVehicleNumberAndIdNot(String vehicleNumber, Integer id);

}