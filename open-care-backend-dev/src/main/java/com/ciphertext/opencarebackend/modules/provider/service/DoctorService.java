package com.ciphertext.opencarebackend.modules.provider.service;
import com.ciphertext.opencarebackend.modules.provider.dto.filter.DoctorFilter;
import com.ciphertext.opencarebackend.modules.provider.dto.request.DoctorRequest;
import com.ciphertext.opencarebackend.entity.Doctor;
import com.ciphertext.opencarebackend.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * @author Sadman
 */
public interface DoctorService {
    Long getDoctorCount();
    List<Doctor> getAllDoctors();
    Page<Doctor> getPaginatedDataWithFilters(DoctorFilter doctorFilter, Pageable pagingSort);
    Doctor getDoctorById(Long id) throws ResourceNotFoundException;
    Doctor getPublicDoctorById(Long id) throws ResourceNotFoundException;
    Doctor getDoctorByEmail(String email) throws ResourceNotFoundException;
    Doctor getPublicDoctorByEmail(String email) throws ResourceNotFoundException;
    Doctor getDoctorByUsername(String username) throws ResourceNotFoundException;
    Doctor getPublicDoctorByUsername(String username) throws ResourceNotFoundException;
    Doctor getDoctorByBmdcNo(String bmdcNo) throws ResourceNotFoundException;
    Doctor getPublicDoctorByBmdcNo(String bmdcNo) throws ResourceNotFoundException;
    Doctor createDoctor(DoctorRequest request);
    Doctor updateDoctor(Long id, DoctorRequest request);
    void deleteDoctorById(Long doctorId);
    void verifyDoctor(Long id);
    void activateDoctor(Long id);
    void deactivateDoctor(Long id);
    void approveDoctor(Long id);

    // Enhanced search methods
    List<Map<String, Object>> quickSearch(String query, int limit);
    Map<String, List<String>> getSearchSuggestions();

    String createDoctorUser(Long doctorId);
}
