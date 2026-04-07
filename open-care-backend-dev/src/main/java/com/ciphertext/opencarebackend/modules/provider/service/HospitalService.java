package com.ciphertext.opencarebackend.modules.provider.service;
import com.ciphertext.opencarebackend.modules.provider.dto.filter.HospitalFilter;
import com.ciphertext.opencarebackend.modules.provider.dto.request.HospitalRequest;
import com.ciphertext.opencarebackend.exception.ResourceNotFoundException;
import com.ciphertext.opencarebackend.entity.Hospital;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author Sadman
 */
/**
 * Flow note: HospitalService belongs to the provider doctor/hospital module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public interface HospitalService {
    Long getHospitalCount();
    List<Hospital> getAllHospitals();
    Page<Hospital> getPaginatedDataWithFilters(HospitalFilter hospitalFilter, Pageable pageable);
    Hospital getHospitalById(Integer id) throws ResourceNotFoundException;
    Hospital getHospitalByRegistrationCode(String registrationCode) throws ResourceNotFoundException;
    Hospital createHospital(HospitalRequest request);
    Hospital updateHospital(Integer id, HospitalRequest request);
    void deleteHospital(Integer id);
    void activateHospital(Integer id);
    void deactivateHospital(Integer id);
}
