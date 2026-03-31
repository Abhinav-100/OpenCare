package com.ciphertext.opencarebackend.modules.provider.service.impl;
import com.ciphertext.opencarebackend.modules.provider.dto.request.DoctorWorkplaceBatchRequest;
import com.ciphertext.opencarebackend.modules.provider.dto.request.DoctorWorkplaceRequest;
import com.ciphertext.opencarebackend.modules.provider.dto.response.DoctorWorkplaceResponse;
import com.ciphertext.opencarebackend.modules.provider.dto.response.MedicalSpecialityResponse;
import com.ciphertext.opencarebackend.entity.Doctor;
import com.ciphertext.opencarebackend.entity.DoctorWorkplace;
import com.ciphertext.opencarebackend.exception.BadRequestException;
import com.ciphertext.opencarebackend.exception.DuplicateResourceException;
import com.ciphertext.opencarebackend.exception.ResourceNotFoundException;
import com.ciphertext.opencarebackend.mapper.DoctorWorkplaceMapper;
import com.ciphertext.opencarebackend.modules.provider.repository.DoctorRepository;
import com.ciphertext.opencarebackend.modules.provider.repository.DoctorWorkplaceRepository;
import com.ciphertext.opencarebackend.modules.provider.repository.HospitalRepository;
import com.ciphertext.opencarebackend.modules.provider.repository.InstitutionRepository;
import com.ciphertext.opencarebackend.modules.provider.repository.MedicalSpecialityRepository;
import com.ciphertext.opencarebackend.modules.provider.service.DoctorWorkplaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class DoctorWorkplaceServiceImpl implements DoctorWorkplaceService {
    private final DoctorWorkplaceRepository doctorWorkplaceRepository;
    private final DoctorRepository doctorRepository;
    private final HospitalRepository hospitalRepository;
    private final InstitutionRepository institutionRepository;
    private final MedicalSpecialityRepository medicalSpecialityRepository;
    private final DoctorWorkplaceMapper doctorWorkplaceMapper;

    @Override
    public List<DoctorWorkplace> getDoctorWorkplacesByDoctorId(Long doctorId) {
        if (doctorId == null || doctorId <= 0) {
            throw new BadRequestException("Doctor ID must be positive");
        }
        log.info("Fetching all doctor workplaces");
        List<DoctorWorkplace> doctorWorkplaces = doctorWorkplaceRepository.findByDoctorId(doctorId);
        log.info("Retrieved {} doctor workplaces", doctorWorkplaces.size());
        return doctorWorkplaces;
    }

    @Override
    public List<DoctorWorkplace> getDoctorWorkplacesByHospitalId(Integer hospitalId) {
        log.info("Fetching doctor workplaces for hospital ID: {}", hospitalId);
        List<DoctorWorkplace> doctorWorkplaces = doctorWorkplaceRepository.findByHospital_Id(hospitalId);
        log.info("Retrieved {} doctor workplaces for hospital ID: {}", doctorWorkplaces.size(), hospitalId);
        return doctorWorkplaces;
    }

    @Override
    public DoctorWorkplace getDoctorWorkplaceById(Long id) throws ResourceNotFoundException {
        if (id == null || id <= 0) {
            log.error("Invalid doctor workplace ID: {}", id);
            throw new BadRequestException("Doctor Workplace ID must be positive");
        }

        log.info("Fetching doctor workplace with id: {}", id);
        return doctorWorkplaceRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Doctor Workplace not found with id: {}", id);
                    return new ResourceNotFoundException("Doctor Workplace not found with id: " + id);
                });
    }

    @Override
    public DoctorWorkplace getDoctorWorkplaceByIdAndDoctorId(Long doctorId, Long id) throws ResourceNotFoundException {
        validatePositiveId(doctorId, "Doctor");
        validatePositiveId(id, "Doctor workplace");

        log.info("Fetching doctor workplace with id: {} for doctorId: {}", id, doctorId);
        return doctorWorkplaceRepository.findByIdAndDoctorId(id, doctorId)
                .orElseThrow(() -> {
                    log.error("Doctor workplace not found with id: {} for doctorId: {}", id, doctorId);
                    return new ResourceNotFoundException("Doctor workplace not found with id: " + id);
                });
    }

    @Override
    public DoctorWorkplace createDoctorWorkplace(DoctorWorkplace doctorWorkplace) {
        log.info("Creating doctor workplace: {}", doctorWorkplace);
        if (doctorWorkplace == null || doctorWorkplace.getDoctor() == null || doctorWorkplace.getDoctor().getId() == null) {
            throw new BadRequestException("Doctor ID is required");
        }

        Long doctorId = doctorWorkplace.getDoctor().getId();
        validatePositiveId(doctorId, "Doctor");

        validateDateRange(doctorWorkplace.getStartDate(), doctorWorkplace.getEndDate());

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + doctorId));
        doctorWorkplace.setDoctor(doctor);

        Integer hospitalId = doctorWorkplace.getHospital() != null ? doctorWorkplace.getHospital().getId() : null;
        if (hospitalId != null && doctorWorkplaceRepository.existsByDoctorIdAndHospitalId(doctorId, hospitalId)) {
            throw new DuplicateResourceException("This workplace at the same hospital already exists for this doctor");
        }

        applyReferencesFromEntity(doctorWorkplace);
        return doctorWorkplaceRepository.save(doctorWorkplace);
    }

    @Override
    public DoctorWorkplace updateDoctorWorkplace(Long doctorId, DoctorWorkplace newDoctorWorkplace, Long doctorWorkplaceId) {
        log.info("Updating doctor workplace: {}", newDoctorWorkplace);
        DoctorWorkplace doctorWorkplace = getDoctorWorkplaceByIdAndDoctorId(doctorId, doctorWorkplaceId);

        if (newDoctorWorkplace.getInstitution() != null) {
            doctorWorkplace.setInstitution(newDoctorWorkplace.getInstitution());
        }
        if (newDoctorWorkplace.getHospital() != null) {
            Integer hospitalId = newDoctorWorkplace.getHospital().getId();
            if (hospitalId != null
                    && doctorWorkplaceRepository.existsByDoctorIdAndHospitalId(doctorId, hospitalId)
                    && (doctorWorkplace.getHospital() == null || !hospitalId.equals(doctorWorkplace.getHospital().getId()))) {
                throw new DuplicateResourceException("This workplace at the same hospital already exists for this doctor");
            }
            doctorWorkplace.setHospital(newDoctorWorkplace.getHospital());
        }
        if (newDoctorWorkplace.getMedicalSpeciality() != null) {
            doctorWorkplace.setMedicalSpeciality(newDoctorWorkplace.getMedicalSpeciality());
        }
        if (newDoctorWorkplace.getDoctorPosition() != null) {
            doctorWorkplace.setDoctorPosition(newDoctorWorkplace.getDoctorPosition());
        }
        if (newDoctorWorkplace.getTeacherPosition() != null) {
            doctorWorkplace.setTeacherPosition(newDoctorWorkplace.getTeacherPosition());
        }
        if (newDoctorWorkplace.getStartDate() != null) {
            doctorWorkplace.setStartDate(newDoctorWorkplace.getStartDate());
        }
        if (newDoctorWorkplace.getEndDate() != null) {
            doctorWorkplace.setEndDate(newDoctorWorkplace.getEndDate());
        }

        validateDateRange(doctorWorkplace.getStartDate(), doctorWorkplace.getEndDate());
        applyReferencesFromEntity(doctorWorkplace);
        return doctorWorkplaceRepository.save(doctorWorkplace);
    }

    @Override
    public void deleteDoctorWorkplaceById(Long doctorWorkplaceId) {
        log.info("Deleting doctor workplace with id: {}", doctorWorkplaceId);
        if (doctorWorkplaceId == null || doctorWorkplaceId <= 0) {
            throw new BadRequestException("Doctor workplace ID must be positive");
        }
        DoctorWorkplace existing = getDoctorWorkplaceById(doctorWorkplaceId);
        doctorWorkplaceRepository.delete(existing);
    }

    @Override
    public List<MedicalSpecialityResponse> getTopMedicalSpecialities(Integer limit) {
        List<Object[]> results = doctorWorkplaceRepository.findMedicalSpecialitiesWithDoctorCount(limit);
        return results.stream()
                .map(this::mapToMedicalSpecialityResponse)
                .collect(Collectors.toList());
    }

    private MedicalSpecialityResponse mapToMedicalSpecialityResponse(Object[] result) {
        MedicalSpecialityResponse response = new MedicalSpecialityResponse();
        response.setId((Integer) result[0]);
        response.setName((String) result[1]);
        response.setBnName((String) result[2]);
        response.setIcon((String) result[3]);
        response.setDoctorCount(((Long) result[4]).intValue());
        return response;
    }

    @Override
    public List<DoctorWorkplaceResponse> batchUpsertDoctorWorkplaces(Long doctorId, List<DoctorWorkplaceBatchRequest> requests) {
        log.info("Batch upserting {} workplaces for doctor ID: {}", requests.size(), doctorId);

        validatePositiveId(doctorId, "Doctor");

        for (DoctorWorkplaceBatchRequest request : requests) {
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
        List<DoctorWorkplaceBatchRequest> createRequests = new ArrayList<>();
        List<DoctorWorkplaceBatchRequest> updateRequests = new ArrayList<>();

        for (DoctorWorkplaceBatchRequest request : requests) {
            if (request.isUpdate()) {
                updateRequests.add(request);
            } else {
                createRequests.add(request);
            }
        }

        List<DoctorWorkplace> resultWorkplaces = new ArrayList<>();

        // Handle updates if any
        if (!updateRequests.isEmpty()) {
            resultWorkplaces.addAll(processUpdates(doctorId, updateRequests));
        }

        // Handle creates if any
        if (!createRequests.isEmpty()) {
            resultWorkplaces.addAll(processCreates(doctorId, doctor, createRequests));
        }

        log.info("Successfully batch upserted {} workplaces for doctor ID: {}", resultWorkplaces.size(), doctorId);

        // Map to responses
        return resultWorkplaces.stream()
                .map(doctorWorkplaceMapper::toResponse)
                .toList();
    }

    /**
     * Process batch updates with optimized DB calls.
     */
    private List<DoctorWorkplace> processUpdates(Long doctorId, List<DoctorWorkplaceBatchRequest> updateRequests) {
        log.debug("Processing {} update requests", updateRequests.size());

        // Extract all IDs for batch query
        List<Long> updateIds = updateRequests.stream()
                .map(DoctorWorkplaceBatchRequest::getId)
                .toList();

        // Single DB call: Fetch all existing workplaces to update
        List<DoctorWorkplace> existingWorkplaces = doctorWorkplaceRepository.findByDoctorIdAndIdIn(doctorId, updateIds);

        // Create map for O(1) lookup
        Map<Long, DoctorWorkplace> existingWorkplacesMap = existingWorkplaces.stream()
                .collect(Collectors.toMap(DoctorWorkplace::getId, w -> w));

        // Validate all requested IDs exist
        for (Long id : updateIds) {
            if (!existingWorkplacesMap.containsKey(id)) {
                log.error("Workplace not found with ID: {} for doctor ID: {}", id, doctorId);
                throw new ResourceNotFoundException(
                        "Workplace not found with ID: " + id + " for doctor ID: " + doctorId);
            }
        }

        // Update entities
        for (DoctorWorkplaceBatchRequest request : updateRequests) {
            DoctorWorkplace workplace = existingWorkplacesMap.get(request.getId());

            // Validate date range
            validateDateRange(request.getStartDate(), request.getEndDate());

            // Update fields
            updateWorkplaceFields(workplace, request);
        }

        // Single batch DB call: Save all updates
        return doctorWorkplaceRepository.saveAll(existingWorkplaces);
    }

    /**
     * Process batch creates with optimized DB calls.
     */
    private List<DoctorWorkplace> processCreates(Long doctorId, Doctor doctor, List<DoctorWorkplaceBatchRequest> createRequests) {
        log.debug("Processing {} create requests", createRequests.size());

        // Extract hospital IDs for batch duplicate check
        List<Long> hospitalIds = createRequests.stream()
                .map(DoctorWorkplaceBatchRequest::getHospitalId)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .toList();

        // Single DB call: Check for existing workplaces (duplicates)
        List<DoctorWorkplace> existingWorkplaces = List.of();
        if (!hospitalIds.isEmpty()) {
            existingWorkplaces = doctorWorkplaceRepository.findByDoctorIdAndHospitalIdIn(doctorId, hospitalIds);
        }

        // Create set for O(1) duplicate lookup (hospital-based duplicates)
        // Note: Hospital ID is Integer, but we use Long in the request DTO
        Map<Integer, DoctorWorkplace> existingHospitals = existingWorkplaces.stream()
                .filter(w -> w.getHospital() != null)
                .collect(Collectors.toMap(
                        w -> w.getHospital().getId(),
                        w -> w,
                        (existing, replacement) -> existing, // Keep first if duplicate keys
                        java.util.HashMap::new
                ));

        // Validate and map to entities
        List<DoctorWorkplace> workplacesToCreate = new ArrayList<>();

        for (DoctorWorkplaceBatchRequest request : createRequests) {
            // Check for duplicates using in-memory map (if hospital is specified)
            if (request.getHospitalId() != null && existingHospitals.containsKey(request.getHospitalId().intValue())) {
                log.warn("Duplicate workplace detected for doctor ID: {}, hospital ID: {}",
                        doctorId, request.getHospitalId());
                throw new DuplicateResourceException(
                        "This workplace at the same hospital already exists for this doctor");
            }

            // Validate date range
            validateDateRange(request.getStartDate(), request.getEndDate());

            // Create new entity
            DoctorWorkplace workplace = new DoctorWorkplace();
            workplace.setDoctor(doctor);
            updateWorkplaceFields(workplace, request);

            workplacesToCreate.add(workplace);
        }

        // Single batch DB call: Save all creates
        return doctorWorkplaceRepository.saveAll(workplacesToCreate);
    }

    /**
     * Update workplace fields from batch request.
     */
    private void updateWorkplaceFields(DoctorWorkplace workplace, DoctorWorkplaceBatchRequest request) {
        // Map foreign key IDs - mapper will handle the actual entity references
        DoctorWorkplaceRequest tempRequest = new DoctorWorkplaceRequest();
        tempRequest.setDoctorId(request.getDoctorId());
        tempRequest.setMedicalSpecialityId(request.getMedicalSpecialityId());
        tempRequest.setInstitutionId(request.getInstitutionId());
        tempRequest.setHospitalId(request.getHospitalId());
        tempRequest.setDoctorPosition(request.getDoctorPosition());
        tempRequest.setTeacherPosition(request.getTeacherPosition());
        tempRequest.setStartDate(request.getStartDate());
        tempRequest.setEndDate(request.getEndDate());
        tempRequest.setDescription(request.getDescription());

        doctorWorkplaceMapper.partialUpdate(tempRequest, workplace);
        applyReferences(workplace, tempRequest, workplace.getDoctor());
    }

    /**
     * Validate date range.
     */
    private void validateDateRange(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new BadRequestException("End date cannot be before start date");
        }
    }

    /**
     * Validate positive ID.
     */
    private void validatePositiveId(Long id, String entityName) {
        if (id == null || id <= 0) {
            throw new BadRequestException(entityName + " ID must be positive");
        }
    }

    private Integer toIntId(Long id, String entityName) {
        if (id == null) {
            return null;
        }
        if (id > Integer.MAX_VALUE) {
            throw new BadRequestException(entityName + " ID is too large");
        }
        return id.intValue();
    }

    private void applyReferences(DoctorWorkplace workplace, DoctorWorkplaceRequest request, Doctor doctor) {
        if (doctor != null) {
            workplace.setDoctor(doctor);
        } else if (request.getDoctorId() != null) {
            workplace.setDoctor(doctorRepository.getReferenceById(request.getDoctorId()));
        }
        if (request.getMedicalSpecialityId() != null) {
            Integer specialityId = toIntId(request.getMedicalSpecialityId(), "Medical speciality");
            workplace.setMedicalSpeciality(medicalSpecialityRepository.getReferenceById(specialityId));
        }
        if (request.getInstitutionId() != null) {
            Integer institutionId = toIntId(request.getInstitutionId(), "Institution");
            workplace.setInstitution(institutionRepository.getReferenceById(institutionId));
        }
        if (request.getHospitalId() != null) {
            Integer hospitalId = toIntId(request.getHospitalId(), "Hospital");
            workplace.setHospital(hospitalRepository.getReferenceById(hospitalId));
        }
    }

    private void applyReferencesFromEntity(DoctorWorkplace workplace) {
        if (workplace.getMedicalSpeciality() != null && workplace.getMedicalSpeciality().getId() != null) {
            workplace.setMedicalSpeciality(medicalSpecialityRepository.getReferenceById(workplace.getMedicalSpeciality().getId()));
        }
        if (workplace.getInstitution() != null && workplace.getInstitution().getId() != null) {
            workplace.setInstitution(institutionRepository.getReferenceById(workplace.getInstitution().getId()));
        }
        if (workplace.getHospital() != null && workplace.getHospital().getId() != null) {
            workplace.setHospital(hospitalRepository.getReferenceById(workplace.getHospital().getId()));
        }
    }

}