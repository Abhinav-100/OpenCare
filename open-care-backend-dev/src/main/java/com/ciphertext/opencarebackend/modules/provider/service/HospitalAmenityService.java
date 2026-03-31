package com.ciphertext.opencarebackend.modules.provider.service;
import com.ciphertext.opencarebackend.modules.provider.dto.filter.AmenityFilter;
import com.ciphertext.opencarebackend.entity.HospitalAmenity;
import com.ciphertext.opencarebackend.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author Sadman
 */
public interface HospitalAmenityService {
    Page<HospitalAmenity> getPaginatedDataWithFilters(AmenityFilter amenityFilter, Pageable pagingSort);
    List<HospitalAmenity> getHospitalAmenitiesByHospitalId(Long hospitalId);
    HospitalAmenity getHospitalAmenityById(Long id) throws ResourceNotFoundException;
    HospitalAmenity getHospitalAmenityByIdAndHospitalId(Long hospitalId, Long id) throws ResourceNotFoundException;
    HospitalAmenity createHospitalAmenity(HospitalAmenity hospitalAmenity);
    HospitalAmenity updateHospitalAmenity(HospitalAmenity hospitalAmenity, Long hospitalAmenityId);
    void deleteHospitalAmenityById(Long hospitalAmenityId);
}
