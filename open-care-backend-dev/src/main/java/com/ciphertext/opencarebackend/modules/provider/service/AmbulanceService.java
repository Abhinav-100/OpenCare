package com.ciphertext.opencarebackend.modules.provider.service;
import com.ciphertext.opencarebackend.modules.provider.dto.filter.AmbulanceFilter;
import com.ciphertext.opencarebackend.entity.Ambulance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AmbulanceService {
    Page<Ambulance> getPaginatedDataWithFilters(AmbulanceFilter ambulanceFilter, Pageable pageable);

    List<Ambulance> getAllAmbulance();

    Ambulance getAmbulanceById(Integer id);

    Ambulance createAmbulance(Ambulance ambulance);

    Ambulance updateAmbulanceById(Ambulance ambulance, Integer id);

    void deleteAmbulanceById(Integer id);
}
