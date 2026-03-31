package com.ciphertext.opencarebackend.modules.provider.service;
import com.ciphertext.opencarebackend.modules.provider.dto.filter.MedicalSpecialityFilter;
import com.ciphertext.opencarebackend.modules.provider.dto.request.MedicalSpecialityRequest;
import com.ciphertext.opencarebackend.exception.ResourceNotFoundException;
import com.ciphertext.opencarebackend.entity.MedicalSpeciality;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author Sadman
 */
public interface MedicalSpecialityService {
    List<MedicalSpeciality> getAllSpecialities();
    MedicalSpeciality getSpecialityById(int id) throws ResourceNotFoundException;
    Page<MedicalSpeciality> getPaginatedDataWithFilters(MedicalSpecialityFilter filter, Pageable pageable);
    MedicalSpeciality createSpeciality(MedicalSpeciality medicalSpeciality);
    MedicalSpeciality updateSpeciality(MedicalSpecialityRequest request, int id) throws ResourceNotFoundException;
    void deleteSpeciality(int id) throws ResourceNotFoundException;
    void refreshAll();
    void refreshOne(int id);
}
