package com.ciphertext.opencarebackend.modules.provider.service.impl;
import com.ciphertext.opencarebackend.modules.provider.dto.request.DoctorDegreeBatchRequest;
import com.ciphertext.opencarebackend.modules.provider.dto.request.DoctorDegreeRequest;
import com.ciphertext.opencarebackend.modules.provider.dto.response.DoctorDegreeResponse;
import com.ciphertext.opencarebackend.entity.Doctor;
import com.ciphertext.opencarebackend.entity.DoctorDegree;
import com.ciphertext.opencarebackend.exception.BadRequestException;
import com.ciphertext.opencarebackend.exception.DuplicateResourceException;
import com.ciphertext.opencarebackend.exception.ResourceNotFoundException;
import com.ciphertext.opencarebackend.mapper.DoctorDegreeMapper;
import com.ciphertext.opencarebackend.modules.provider.repository.DegreeRepository;
import com.ciphertext.opencarebackend.modules.provider.repository.DoctorDegreeRepository;
import com.ciphertext.opencarebackend.modules.provider.repository.DoctorRepository;
import com.ciphertext.opencarebackend.modules.provider.repository.InstitutionRepository;
import com.ciphertext.opencarebackend.modules.provider.repository.MedicalSpecialityRepository;
import com.ciphertext.opencarebackend.modules.provider.service.DoctorDegreeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service implementation for managing doctor degrees.
 * Handles business logic, validation, and data persistence for doctor degree operations.
 *
 * @author Sadman
 */
@Service
@Slf4j
@RequiredArgsConstructor
/**
 * Flow note: DoctorDegreeServiceImpl belongs to the provider doctor/hospital module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public class DoctorDegreeServiceImpl implements DoctorDegreeService {

    private final DoctorDegreeRepository doctorDegreeRepository;
    private final DoctorRepository doctorRepository;
    private final DegreeRepository degreeRepository;
    private final InstitutionRepository institutionRepository;
    private final MedicalSpecialityRepository medicalSpecialityRepository;
    private final DoctorDegreeMapper doctorDegreeMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<DoctorDegreeResponse> getDoctorDegreesByDoctorId(Long doctorId, Pageable pageable) {
        log.debug("Fetching degrees for doctor ID: {} with pagination", doctorId);

        validateDoctorExists(doctorId);

        Page<DoctorDegree> degrees = doctorDegreeRepository.findByDoctorId(doctorId, pageable);
        log.info("Retrieved {} degrees for doctor ID: {}", degrees.getTotalElements(), doctorId);

        return degrees.map(doctorDegreeMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorDegreeResponse> getDoctorDegreesByDoctorId(Long doctorId) {
        log.debug("Fetching all degrees for doctor ID: {}", doctorId);

        validateDoctorExists(doctorId);

        List<DoctorDegree> degrees = doctorDegreeRepository.findByDoctorId(doctorId);
        log.info("Retrieved {} degrees for doctor ID: {}", degrees.size(), doctorId);

        return degrees.stream()
                .map(doctorDegreeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorDegreeResponse getDoctorDegree(Long doctorId, Long degreeId) throws ResourceNotFoundException {
        log.debug("Fetching degree ID: {} for doctor ID: {}", degreeId, doctorId);

        validatePositiveId(degreeId, "Degree");
        validatePositiveId(doctorId, "Doctor");

        DoctorDegree degree = doctorDegreeRepository.findByIdAndDoctorId(degreeId, doctorId)
                .orElseThrow(() -> {
                    log.error("Degree not found with ID: {} for doctor ID: {}", degreeId, doctorId);
                    return new ResourceNotFoundException(
                            "Degree not found with ID: " + degreeId + " for doctor ID: " + doctorId);
                });

        log.info("Successfully retrieved degree ID: {} for doctor ID: {}", degreeId, doctorId);
        return doctorDegreeMapper.toResponse(degree);
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorDegreeResponse getDoctorDegreeById(Long id) throws ResourceNotFoundException {
        log.debug("Fetching degree with ID: {}", id);

        validatePositiveId(id, "Degree");

        DoctorDegree degree = doctorDegreeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Degree not found with ID: {}", id);
                    return new ResourceNotFoundException("Degree not found with ID: " + id);
                });

        log.info("Successfully retrieved degree ID: {}", id);
        return doctorDegreeMapper.toResponse(degree);
    }

    @Override
    @Transactional
    public DoctorDegreeResponse createDoctorDegree(Long doctorId, DoctorDegreeRequest request) {
        log.info("Creating new degree for doctor ID: {}", doctorId);

        validatePositiveId(doctorId, "Doctor");

        // Validate doctor exists
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> {
                    log.error("Doctor not found with ID: {}", doctorId);
                    return new ResourceNotFoundException("Doctor not found with ID: " + doctorId);
                });

        // Set the doctorId in request to ensure consistency
        request.setDoctorId(doctorId);

        // Validate for duplicate degrees
        if (doctorDegreeRepository.existsByDoctorIdAndDegreeIdAndInstitutionId(
                doctorId, request.getDegreeId(), request.getInstitutionId())) {
            log.warn("Duplicate degree detected for doctor ID: {}, degree ID: {}, institution ID: {}",
                    doctorId, request.getDegreeId(), request.getInstitutionId());
            throw new DuplicateResourceException(
                    "This degree from the same institution already exists for this doctor");
        }

        // Validate date logic
        validateDateRange(request.getStartDate(), request.getEndDate());

        // Map and save
        DoctorDegree degree = doctorDegreeMapper.toEntity(request);
        applyReferences(degree, request, doctor);
        DoctorDegree savedDegree = doctorDegreeRepository.save(degree);

        log.info("Successfully created degree with ID: {} for doctor ID: {}", savedDegree.getId(), doctorId);
        return doctorDegreeMapper.toResponse(savedDegree);
    }

    @Override
    @Transactional
    public DoctorDegreeResponse updateDoctorDegree(Long doctorId, Long degreeId, DoctorDegreeRequest request)
            throws ResourceNotFoundException {
        log.info("Updating degree ID: {} for doctor ID: {}", degreeId, doctorId);

        validatePositiveId(degreeId, "Degree");
        validatePositiveId(doctorId, "Doctor");

        // Find existing degree
        DoctorDegree existingDegree = doctorDegreeRepository.findByIdAndDoctorId(degreeId, doctorId)
                .orElseThrow(() -> {
                    log.error("Degree not found with ID: {} for doctor ID: {}", degreeId, doctorId);
                    return new ResourceNotFoundException(
                            "Degree not found with ID: " + degreeId + " for doctor ID: " + doctorId);
                });

        // Validate date logic
        validateDateRange(request.getStartDate(), request.getEndDate());

        // Update entity using mapper
        doctorDegreeMapper.partialUpdate(request, existingDegree);
        applyReferences(existingDegree, request, null);

        DoctorDegree updatedDegree = doctorDegreeRepository.save(existingDegree);

        log.info("Successfully updated degree ID: {} for doctor ID: {}", degreeId, doctorId);
        return doctorDegreeMapper.toResponse(updatedDegree);
    }

    @Override
    @Transactional
    public void deleteDoctorDegree(Long doctorId, Long degreeId) throws ResourceNotFoundException {
        log.info("Deleting degree ID: {} for doctor ID: {}", degreeId, doctorId);

        validatePositiveId(degreeId, "Degree");
        validatePositiveId(doctorId, "Doctor");

        // Verify degree exists for this doctor
        if (!doctorDegreeRepository.existsByIdAndDoctorId(degreeId, doctorId)) {
            log.error("Degree not found with ID: {} for doctor ID: {}", degreeId, doctorId);
            throw new ResourceNotFoundException(
                    "Degree not found with ID: " + degreeId + " for doctor ID: " + doctorId);
        }

        doctorDegreeRepository.deleteById(degreeId);
        log.info("Successfully deleted degree ID: {} for doctor ID: {}", degreeId, doctorId);
    }

    @Override
    @Transactional
    public List<DoctorDegreeResponse> batchUpsertDoctorDegrees(Long doctorId, List<DoctorDegreeBatchRequest> requests) {
        log.info("Batch upserting {} degrees for doctor ID: {}", requests.size(), doctorId);

        validatePositiveId(doctorId, "Doctor");

        // Single DB call: Verify doctor exists
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> {
                    log.error("Doctor not found with ID: {}", doctorId);
                    return new ResourceNotFoundException("Doctor not found with ID: " + doctorId);
                });

        // Separate requests into creates and updates
        List<DoctorDegreeBatchRequest> createRequests = new ArrayList<>();
        List<DoctorDegreeBatchRequest> updateRequests = new ArrayList<>();

        for (DoctorDegreeBatchRequest request : requests) {
            if (request.isUpdate()) {
                updateRequests.add(request);
            } else {
                createRequests.add(request);
            }
        }

        List<DoctorDegree> resultDegrees = new ArrayList<>();

        // Handle updates if any
        if (!updateRequests.isEmpty()) {
            resultDegrees.addAll(processUpdates(doctorId, updateRequests));
        }

        // Handle creates if any
        if (!createRequests.isEmpty()) {
            resultDegrees.addAll(processCreates(doctorId, doctor, createRequests));
        }

        log.info("Successfully batch upserted {} degrees for doctor ID: {}", resultDegrees.size(), doctorId);

        // Map to responses
        return resultDegrees.stream()
                .map(doctorDegreeMapper::toResponse)
                .toList();
    }

    /**
     * Process batch updates with optimized DB calls.
     */
    private List<DoctorDegree> processUpdates(Long doctorId, List<DoctorDegreeBatchRequest> updateRequests) {
        log.debug("Processing {} update requests", updateRequests.size());

        // Extract all IDs for batch query
        List<Long> updateIds = updateRequests.stream()
                .map(DoctorDegreeBatchRequest::getId)
                .toList();

        // Single DB call: Fetch all existing degrees to update
        List<DoctorDegree> existingDegrees = doctorDegreeRepository.findByDoctorIdAndIdIn(doctorId, updateIds);

        // Create map for O(1) lookup
        Map<Long, DoctorDegree> existingDegreesMap = existingDegrees.stream()
                .collect(Collectors.toMap(DoctorDegree::getId, d -> d));

        // Validate all requested IDs exist
        for (Long id : updateIds) {
            if (!existingDegreesMap.containsKey(id)) {
                log.error("Degree not found with ID: {} for doctor ID: {}", id, doctorId);
                throw new ResourceNotFoundException(
                        "Degree not found with ID: " + id + " for doctor ID: " + doctorId);
            }
        }

        // Update entities
        for (DoctorDegreeBatchRequest request : updateRequests) {
            DoctorDegree degree = existingDegreesMap.get(request.getId());

            // Validate date range
            validateDateRange(request.getStartDate(), request.getEndDate());

            // Update fields
            updateDegreeFields(degree, request);
        }

        // Single batch DB call: Save all updates
        return doctorDegreeRepository.saveAll(existingDegrees);
    }

    /**
     * Process batch creates with optimized DB calls.
     */
    private List<DoctorDegree> processCreates(Long doctorId, Doctor doctor, List<DoctorDegreeBatchRequest> createRequests) {
        log.debug("Processing {} create requests", createRequests.size());

        // Extract degree and institution IDs for batch duplicate check
        List<Long> degreeIds = createRequests.stream()
                .map(DoctorDegreeBatchRequest::getDegreeId)
                .distinct()
                .toList();

        List<Long> institutionIds = createRequests.stream()
                .map(DoctorDegreeBatchRequest::getInstitutionId)
                .distinct()
                .toList();

        // Single DB call: Check for existing degrees (duplicates)
        List<DoctorDegree> existingDegrees = doctorDegreeRepository
                .findByDoctorIdAndDegreeIdInAndInstitutionIdIn(doctorId, degreeIds, institutionIds);

        // Create set for O(1) duplicate lookup
        Map<String, DoctorDegree> existingCombinations = existingDegrees.stream()
                .collect(Collectors.toMap(
                        d -> d.getDegree().getId() + "-" + d.getInstitution().getId(),
                        d -> d
                ));

        // Validate and map to entities
        List<DoctorDegree> degreesToCreate = new ArrayList<>();

        for (DoctorDegreeBatchRequest request : createRequests) {
            // Check for duplicates using in-memory map
            String combination = request.getDegreeId() + "-" + request.getInstitutionId();
            if (existingCombinations.containsKey(combination)) {
                log.warn("Duplicate degree detected for doctor ID: {}, degree ID: {}, institution ID: {}",
                        doctorId, request.getDegreeId(), request.getInstitutionId());
                throw new DuplicateResourceException(
                        "This degree from the same institution already exists for this doctor");
            }

            // Validate date range
            validateDateRange(request.getStartDate(), request.getEndDate());

            // Create new entity
            DoctorDegree degree = new DoctorDegree();
            degree.setDoctor(doctor);
            updateDegreeFields(degree, request);

            degreesToCreate.add(degree);
        }

        // Single batch DB call: Save all creates
        return doctorDegreeRepository.saveAll(degreesToCreate);
    }

    /**
     * Update degree fields from batch request.
     */
    private void updateDegreeFields(DoctorDegree degree, DoctorDegreeBatchRequest request) {
        // Map foreign key IDs - mapper will handle the actual entity references
        DoctorDegreeRequest tempRequest = new DoctorDegreeRequest();
        tempRequest.setDoctorId(request.getDoctorId());
        tempRequest.setDegreeId(request.getDegreeId());
        tempRequest.setMedicalSpecialityId(request.getMedicalSpecialityId());
        tempRequest.setInstitutionId(request.getInstitutionId());
        tempRequest.setStartDate(request.getStartDate());
        tempRequest.setEndDate(request.getEndDate());
        tempRequest.setGrade(request.getGrade());
        tempRequest.setDescription(request.getDescription());

        doctorDegreeMapper.partialUpdate(tempRequest, degree);
        applyReferences(degree, tempRequest, degree.getDoctor());
    }

    // ==================== Private Helper Methods ====================

    /**
     * Validates that a doctor exists in the database.
     *
     * @param doctorId the doctor ID to validate
     * @throws ResourceNotFoundException if doctor doesn't exist
     */
    private void validateDoctorExists(Long doctorId) {
        if (!doctorRepository.existsById(doctorId)) {
            log.error("Doctor not found with ID: {}", doctorId);
            throw new ResourceNotFoundException("Doctor not found with ID: " + doctorId);
        }
    }

    /**
     * Validates that an ID is positive.
     *
     * @param id the ID to validate
     * @param resourceName the name of the resource (for error messages)
     * @throws BadRequestException if ID is not positive
     */
    private void validatePositiveId(Long id, String resourceName) {
        if (id == null || id <= 0) {
            log.error("Invalid {} ID: {}", resourceName, id);
            throw new BadRequestException(resourceName + " ID must be positive");
        }
    }

    private Integer toIntId(Long id, String resourceName) {
        if (id == null) {
            return null;
        }
        if (id > Integer.MAX_VALUE) {
            log.error("Invalid {} ID: {}", resourceName, id);
            throw new BadRequestException(resourceName + " ID is too large");
        }
        return id.intValue();
    }

    private void applyReferences(DoctorDegree degree, DoctorDegreeRequest request, Doctor doctor) {
        if (doctor != null) {
            degree.setDoctor(doctor);
        } else if (request.getDoctorId() != null) {
            degree.setDoctor(doctorRepository.getReferenceById(request.getDoctorId()));
        }
        if (request.getDegreeId() != null) {
            Integer degreeId = toIntId(request.getDegreeId(), "Degree");
            degree.setDegree(degreeRepository.getReferenceById(degreeId));
        }
        if (request.getMedicalSpecialityId() != null) {
            Integer specialityId = toIntId(request.getMedicalSpecialityId(), "Medical speciality");
            degree.setMedicalSpeciality(medicalSpecialityRepository.getReferenceById(specialityId));
        }
        if (request.getInstitutionId() != null) {
            Integer institutionId = toIntId(request.getInstitutionId(), "Institution");
            degree.setInstitution(institutionRepository.getReferenceById(institutionId));
        }
    }

    /**
     * Validates that end date is not before start date.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @throws BadRequestException if date range is invalid
     */
    private void validateDateRange(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            log.error("Invalid date range: start date {} is after end date {}", startDate, endDate);
            throw new BadRequestException("End date cannot be before start date");
        }
    }
}