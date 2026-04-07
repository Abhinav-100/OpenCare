package com.ciphertext.opencarebackend.modules.provider.service.impl;
import com.ciphertext.opencarebackend.modules.provider.dto.request.DoctorAssociationBatchRequest;
import com.ciphertext.opencarebackend.modules.provider.dto.response.DoctorAssociationResponse;
import com.ciphertext.opencarebackend.entity.Association;
import com.ciphertext.opencarebackend.entity.Doctor;
import com.ciphertext.opencarebackend.entity.DoctorAssociation;
import com.ciphertext.opencarebackend.exception.BadRequestException;
import com.ciphertext.opencarebackend.exception.DuplicateResourceException;
import com.ciphertext.opencarebackend.exception.ResourceNotFoundException;
import com.ciphertext.opencarebackend.mapper.DoctorAssociationMapper;
import com.ciphertext.opencarebackend.modules.provider.repository.AssociationRepository;
import com.ciphertext.opencarebackend.modules.provider.repository.DoctorAssociationRepository;
import com.ciphertext.opencarebackend.modules.provider.repository.DoctorRepository;
import com.ciphertext.opencarebackend.modules.provider.service.DoctorAssociationService;
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

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
/**
 * Flow note: DoctorAssociationServiceImpl belongs to the provider doctor/hospital module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public class DoctorAssociationServiceImpl implements DoctorAssociationService {
    private final DoctorAssociationRepository doctorAssociationRepository;
    private final DoctorRepository doctorRepository;
    private final AssociationRepository associationRepository;
    private final DoctorAssociationMapper doctorAssociationMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<DoctorAssociation> getPaginatedDataWithFilters(Pageable pageable) {
        log.info("Fetching paginated doctor associations: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        return doctorAssociationRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorAssociation> getAllDoctorAssociations() {
        log.info("Fetching all doctor associations");
        return doctorAssociationRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorAssociation> getDoctorAssociationsByDoctorId(Long doctorId) {
        if (doctorId == null || doctorId <= 0) {
            log.error("Invalid Doctor ID: {}", doctorId);
            throw new BadRequestException("Doctor ID must be positive");
        }

        log.info("Fetching doctor associations for doctor with id: {}", doctorId);
        List<DoctorAssociation> associations = doctorAssociationRepository.findByDoctorId(doctorId);
        log.info("Retrieved {} associations for doctor with id: {}", associations.size(), doctorId);
        return associations;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorAssociation> getDoctorAssociationsByAssociationId(Integer associationId) {
        if (associationId == null || associationId <= 0) {
            log.error("Invalid Association ID: {}", associationId);
            throw new BadRequestException("Association ID must be positive");
        }

        log.info("Fetching doctor associations for association with id: {}", associationId);
        List<DoctorAssociation> associations = doctorAssociationRepository.findByAssociationId(associationId);
        log.info("Retrieved {} associations for association with id: {}", associations.size(), associationId);
        return associations;
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorAssociation getDoctorAssociationById(Long id) throws ResourceNotFoundException {
        if (id == null || id <= 0) {
            log.error("Invalid DoctorAssociation ID: {}", id);
            throw new BadRequestException("DoctorAssociation ID must be positive");
        }

        log.info("Fetching doctor association with id: {}", id);
        return doctorAssociationRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("DoctorAssociation not found with id: {}", id);
                    return new ResourceNotFoundException("DoctorAssociation not found with id: " + id);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorAssociation getDoctorAssociationByIdAndDoctorId(Long doctorId, Long id) throws ResourceNotFoundException {
        validatePositiveId(doctorId, "Doctor");
        validatePositiveId(id, "DoctorAssociation");

        log.info("Fetching doctor association with id: {} for doctorId: {}", id, doctorId);
        return doctorAssociationRepository.findByIdAndDoctorId(id, doctorId)
                .orElseThrow(() -> {
                    log.error("DoctorAssociation not found with id: {} for doctorId: {}", id, doctorId);
                    return new ResourceNotFoundException("DoctorAssociation not found with id: " + id);
                });
    }

    @Override
    public DoctorAssociation createDoctorAssociation(DoctorAssociation doctorAssociation) {
        log.info("Creating doctor association: {}", doctorAssociation);
        if (doctorAssociation == null) {
            throw new BadRequestException("Doctor association payload is required");
        }

        Long doctorId = doctorAssociation.getDoctor() != null ? doctorAssociation.getDoctor().getId() : null;
        Integer associationId = doctorAssociation.getAssociation() != null ? doctorAssociation.getAssociation().getId() : null;

        validatePositiveId(doctorId, "Doctor");
        if (associationId == null || associationId <= 0) {
            throw new BadRequestException("Association ID must be positive");
        }

        validateDateRange(doctorAssociation.getStartDate(), doctorAssociation.getEndDate());

        if (doctorAssociationRepository.existsByDoctorIdAndAssociationId(doctorId, associationId)) {
            throw new DuplicateResourceException("This association already exists for this doctor");
        }

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + doctorId));
        Association association = associationRepository.findById(associationId)
                .orElseThrow(() -> new ResourceNotFoundException("Association not found with ID: " + associationId));

        doctorAssociation.setDoctor(doctor);
        doctorAssociation.setAssociation(association);
        if (doctorAssociation.getIsActive() == null) {
            doctorAssociation.setIsActive(true);
        }

        return doctorAssociationRepository.save(doctorAssociation);
    }

    @Override
    public DoctorAssociation updateDoctorAssociationById(Long doctorId, DoctorAssociation newDoctorAssociation, Long id)
            throws ResourceNotFoundException {
        log.info("Updating doctor association with id: {} for doctorId: {}", id, doctorId);
        DoctorAssociation existing = getDoctorAssociationByIdAndDoctorId(doctorId, id);

        if (newDoctorAssociation.getAssociation() != null && newDoctorAssociation.getAssociation().getId() != null) {
            Integer associationId = newDoctorAssociation.getAssociation().getId();
            if (doctorAssociationRepository.existsByDoctorIdAndAssociationId(doctorId, associationId)
                    && (existing.getAssociation() == null || !associationId.equals(existing.getAssociation().getId()))) {
                throw new DuplicateResourceException("This association already exists for this doctor");
            }
            Association association = associationRepository.findById(associationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Association not found with ID: " + associationId));
            existing.setAssociation(association);
        }

        if (newDoctorAssociation.getMembershipType() != null) {
            existing.setMembershipType(newDoctorAssociation.getMembershipType());
        }

        if (newDoctorAssociation.getStartDate() != null) {
            existing.setStartDate(newDoctorAssociation.getStartDate());
        }

        if (newDoctorAssociation.getEndDate() != null) {
            existing.setEndDate(newDoctorAssociation.getEndDate());
        }

        if (newDoctorAssociation.getIsActive() != null) {
            existing.setIsActive(newDoctorAssociation.getIsActive());
        }

        validateDateRange(existing.getStartDate(), existing.getEndDate());
        return doctorAssociationRepository.save(existing);
    }

    @Override
    public void deleteDoctorAssociationById(Long doctorId, Long id) throws ResourceNotFoundException {
        log.info("Deleting doctor association with id: {} for doctorId: {}", id, doctorId);
        DoctorAssociation existing = getDoctorAssociationByIdAndDoctorId(doctorId, id);
        doctorAssociationRepository.delete(existing);
    }

    @Override
    public List<DoctorAssociationResponse> batchUpsertDoctorAssociations(Long doctorId, List<DoctorAssociationBatchRequest> requests) {
        log.info("Batch upserting {} associations for doctor ID: {}", requests.size(), doctorId);

        validatePositiveId(doctorId, "Doctor");

        for (DoctorAssociationBatchRequest request : requests) {
            if (request.getDoctorId() != null && !request.getDoctorId().equals(doctorId)) {
                throw new BadRequestException("Doctor ID mismatch for batch request");
            }
        }

        // Single DB call: Verify doctor exists
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> {
                    log.error("Doctor not found with ID: {}", doctorId);
                    return new ResourceNotFoundException("Doctor not found with ID: " + doctorId);
                });

        // Separate requests into creates and updates
        List<DoctorAssociationBatchRequest> createRequests = new ArrayList<>();
        List<DoctorAssociationBatchRequest> updateRequests = new ArrayList<>();

        for (DoctorAssociationBatchRequest request : requests) {
            if (request.isUpdate()) {
                updateRequests.add(request);
            } else {
                createRequests.add(request);
            }
        }

        List<DoctorAssociation> resultAssociations = new ArrayList<>();

        // Handle updates if any
        if (!updateRequests.isEmpty()) {
            resultAssociations.addAll(processUpdates(doctorId, updateRequests));
        }

        // Handle creates if any
        if (!createRequests.isEmpty()) {
            resultAssociations.addAll(processCreates(doctorId, doctor, createRequests));
        }

        log.info("Successfully batch upserted {} associations for doctor ID: {}", resultAssociations.size(), doctorId);

        // Map to responses
        return resultAssociations.stream()
                .map(doctorAssociationMapper::toResponse)
                .toList();
    }

    /**
     * Process batch updates with optimized DB calls.
     */
    private List<DoctorAssociation> processUpdates(Long doctorId, List<DoctorAssociationBatchRequest> updateRequests) {
        log.debug("Processing {} update requests", updateRequests.size());

        // Extract all IDs for batch query
        List<Long> updateIds = updateRequests.stream()
                .map(DoctorAssociationBatchRequest::getId)
                .toList();

        // Single DB call: Fetch all existing associations to update
        List<DoctorAssociation> existingAssociations = doctorAssociationRepository.findByDoctorIdAndIdIn(doctorId, updateIds);

        // Create map for O(1) lookup
        Map<Long, DoctorAssociation> existingAssociationsMap = existingAssociations.stream()
                .collect(Collectors.toMap(DoctorAssociation::getId, a -> a));

        // Validate all requested IDs exist
        for (Long id : updateIds) {
            if (!existingAssociationsMap.containsKey(id)) {
                log.error("Association not found with ID: {} for doctor ID: {}", id, doctorId);
                throw new ResourceNotFoundException(
                        "Association not found with ID: " + id + " for doctor ID: " + doctorId);
            }
        }

        // Update entities
        for (DoctorAssociationBatchRequest request : updateRequests) {
            DoctorAssociation association = existingAssociationsMap.get(request.getId());

            // Validate date range
            validateDateRange(request.getStartDate(), request.getEndDate());

            // Update fields
            updateAssociationFields(association, request);
        }

        // Single batch DB call: Save all updates
        return doctorAssociationRepository.saveAll(existingAssociations);
    }

    /**
     * Process batch creates with optimized DB calls.
     */
    private List<DoctorAssociation> processCreates(Long doctorId, Doctor doctor, List<DoctorAssociationBatchRequest> createRequests) {
        log.debug("Processing {} create requests", createRequests.size());

        // Extract association IDs for batch duplicate check
        List<Integer> associationIds = createRequests.stream()
                .map(DoctorAssociationBatchRequest::getAssociationId)
                .distinct()
                .toList();

        // Single DB call: Check for existing associations (duplicates)
        List<DoctorAssociation> existingAssociations = doctorAssociationRepository
                .findByDoctorIdAndAssociationIdIn(doctorId, associationIds);

        // Create set for O(1) duplicate lookup
        Map<Integer, DoctorAssociation> existingCombinations = existingAssociations.stream()
                .collect(Collectors.toMap(
                        a -> a.getAssociation().getId(),
                        a -> a
                ));

        // Validate and map to entities
        List<DoctorAssociation> associationsToCreate = new ArrayList<>();

        for (DoctorAssociationBatchRequest request : createRequests) {
            // Check for duplicates using in-memory map
            if (existingCombinations.containsKey(request.getAssociationId())) {
                log.warn("Duplicate association detected for doctor ID: {}, association ID: {}",
                        doctorId, request.getAssociationId());
                throw new DuplicateResourceException(
                        "This association already exists for this doctor");
            }

            // Validate date range
            validateDateRange(request.getStartDate(), request.getEndDate());

            // Create new entity
            DoctorAssociation association = new DoctorAssociation();
            association.setDoctor(doctor);
            updateAssociationFields(association, request);

            associationsToCreate.add(association);
        }

        // Single batch DB call: Save all creates
        return doctorAssociationRepository.saveAll(associationsToCreate);
    }

    /**
     * Update association fields from batch request.
     */
    private void updateAssociationFields(DoctorAssociation association, DoctorAssociationBatchRequest request) {
        // Fetch association entity
        Association associationEntity = associationRepository.findById(request.getAssociationId())
                .orElseThrow(() -> {
                    log.error("Association not found with ID: {}", request.getAssociationId());
                    return new ResourceNotFoundException("Association not found with ID: " + request.getAssociationId());
                });

        association.setAssociation(associationEntity);
        association.setMembershipType(request.getMembershipType());
        association.setStartDate(request.getStartDate());
        association.setEndDate(request.getEndDate());
        association.setIsActive(request.getIsActive());
    }

    /**
     * Validate date range.
     */
    private void validateDateRange(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            log.error("End date {} cannot be before start date {}", endDate, startDate);
            throw new BadRequestException("End date cannot be before start date");
        }
    }

    /**
     * Validate that an ID is positive.
     */
    private void validatePositiveId(Long id, String entityName) {
        if (id == null || id <= 0) {
            log.error("Invalid {} ID: {}", entityName, id);
            throw new BadRequestException(entityName + " ID must be positive");
        }
    }
}
