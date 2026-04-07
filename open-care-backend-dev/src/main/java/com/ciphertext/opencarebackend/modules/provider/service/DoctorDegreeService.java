package com.ciphertext.opencarebackend.modules.provider.service;
import com.ciphertext.opencarebackend.modules.provider.dto.request.DoctorDegreeBatchRequest;
import com.ciphertext.opencarebackend.modules.provider.dto.request.DoctorDegreeRequest;
import com.ciphertext.opencarebackend.modules.provider.dto.response.DoctorDegreeResponse;
import com.ciphertext.opencarebackend.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for managing doctor degrees.
 * Provides business logic for CRUD operations on doctor degree records.
 *
 * @author Sadman
 */
/**
 * Flow note: DoctorDegreeService belongs to the provider doctor/hospital module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public interface DoctorDegreeService {

    /**
     * Get all degrees for a specific doctor with pagination.
     *
     * @param doctorId the doctor ID
     * @param pageable pagination information
     * @return page of doctor degree responses
     */
    Page<DoctorDegreeResponse> getDoctorDegreesByDoctorId(Long doctorId, Pageable pageable);

    /**
     * Get all degrees for a specific doctor without pagination.
     *
     * @param doctorId the doctor ID
     * @return list of doctor degree responses
     */
    List<DoctorDegreeResponse> getDoctorDegreesByDoctorId(Long doctorId);

    /**
     * Get a specific degree by ID and doctor ID.
     *
     * @param doctorId the doctor ID
     * @param degreeId the degree ID
     * @return doctor degree response
     * @throws ResourceNotFoundException if degree not found
     */
    DoctorDegreeResponse getDoctorDegree(Long doctorId, Long degreeId) throws ResourceNotFoundException;

    /**
     * Get a degree by ID only (backward compatibility).
     *
     * @param id the degree ID
     * @return doctor degree response
     * @throws ResourceNotFoundException if degree not found
     */
    DoctorDegreeResponse getDoctorDegreeById(Long id) throws ResourceNotFoundException;

    /**
     * Create a new doctor degree.
     *
     * @param doctorId the doctor ID
     * @param request the degree request data
     * @return created doctor degree response
     */
    DoctorDegreeResponse createDoctorDegree(Long doctorId, DoctorDegreeRequest request);

    /**
     * Update an existing doctor degree.
     *
     * @param doctorId the doctor ID
     * @param degreeId the degree ID
     * @param request the updated degree data
     * @return updated doctor degree response
     * @throws ResourceNotFoundException if degree not found
     */
    DoctorDegreeResponse updateDoctorDegree(Long doctorId, Long degreeId, DoctorDegreeRequest request) throws ResourceNotFoundException;

    /**
     * Delete a doctor degree.
     *
     * @param doctorId the doctor ID
     * @param degreeId the degree ID
     * @throws ResourceNotFoundException if degree not found
     */
    void deleteDoctorDegree(Long doctorId, Long degreeId) throws ResourceNotFoundException;

    /**
     * Batch create/update doctor degrees.
     * If request has id, updates existing degree; otherwise creates new degree.
     * Optimized with minimal database calls.
     *
     * @param doctorId the doctor ID
     * @param requests list of batch requests (mix of create and update)
     * @return list of created/updated doctor degree responses
     */
    List<DoctorDegreeResponse> batchUpsertDoctorDegrees(Long doctorId, List<DoctorDegreeBatchRequest> requests);
}
