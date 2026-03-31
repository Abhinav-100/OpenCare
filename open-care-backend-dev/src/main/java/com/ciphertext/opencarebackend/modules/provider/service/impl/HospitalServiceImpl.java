package com.ciphertext.opencarebackend.modules.provider.service.impl;
import com.ciphertext.opencarebackend.modules.provider.dto.filter.HospitalFilter;
import com.ciphertext.opencarebackend.modules.provider.dto.request.HospitalRequest;
import com.ciphertext.opencarebackend.entity.Hospital;
import com.ciphertext.opencarebackend.entity.Tag;
import com.ciphertext.opencarebackend.enums.HospitalType;
import com.ciphertext.opencarebackend.enums.OrganizationType;
import com.ciphertext.opencarebackend.exception.BadRequestException;
import com.ciphertext.opencarebackend.exception.DuplicateResourceException;
import com.ciphertext.opencarebackend.exception.ResourceNotFoundException;
import com.ciphertext.opencarebackend.exception.UnprocessableEntityException;
import com.ciphertext.opencarebackend.mapper.HospitalMapper;
import com.ciphertext.opencarebackend.modules.provider.repository.DoctorWorkplaceRepository;
import com.ciphertext.opencarebackend.modules.provider.repository.HospitalMedicalTestRepository;
import com.ciphertext.opencarebackend.modules.provider.repository.HospitalRepository;
import com.ciphertext.opencarebackend.modules.shared.repository.specification.Filter;
import com.ciphertext.opencarebackend.modules.provider.service.HospitalService;
import com.ciphertext.opencarebackend.modules.catalog.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.ciphertext.opencarebackend.modules.shared.repository.specification.QueryFilterUtils.*;
import static com.ciphertext.opencarebackend.modules.shared.repository.specification.QueryOperator.*;
import static com.ciphertext.opencarebackend.modules.shared.repository.specification.SpecificationBuilder.createSpecification;
import static org.springframework.data.jpa.domain.Specification.where;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class HospitalServiceImpl implements HospitalService {

    private final HospitalRepository hospitalRepository;
    private final HospitalMedicalTestRepository hospitalMedicalTestRepository;
    private final DoctorWorkplaceRepository doctorWorkplaceRepository;
    private final TagService tagService;
    private final HospitalMapper hospitalMapper;

    @Override
    @Transactional(readOnly = true)
    public Long getHospitalCount() {
        return hospitalRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Hospital> getAllHospitals() {
        log.info("Fetching all hospitals");
        List<Hospital> hospitals = hospitalRepository.findAll();
        log.info("Retrieved {} hospitals", hospitals.size());
        return hospitals;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Hospital> getPaginatedDataWithFilters(HospitalFilter filter, Pageable pageable) {
        log.info("Fetching hospitals with filters: {}", filter);
        List<Filter> filterList = generateQueryFilters(filter);

        Specification<Hospital> specification = where(null);
        if (!filterList.isEmpty()) {
            specification = where(createSpecification(filterList.removeFirst()));
            for (Filter input : filterList) {
                specification = specification.and(createSpecification(input));
            }
        }

        Page<Hospital> result = hospitalRepository.findAll(specification, pageable);
        log.info("Retrieved {} hospitals", result.getTotalElements());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Hospital getHospitalById(Integer id) throws ResourceNotFoundException {
        validateId(id);
        log.info("Fetching hospital with id: {}", id);
        return hospitalRepository.findById(id)
            .orElseThrow(() -> {
                log.error("Hospital not found with id: {}", id);
                return new ResourceNotFoundException("Hospital not found with id: " + id);
            });
    }

    @Override
    @Transactional(readOnly = true)
    public Hospital getHospitalByRegistrationCode(String registrationCode) throws ResourceNotFoundException {
        if (registrationCode == null || registrationCode.trim().isEmpty()) {
            throw new BadRequestException("Registration code is required");
        }
        log.info("Fetching hospital with registration code: {}", registrationCode);
        return hospitalRepository.findByRegistrationCode(registrationCode)
            .orElseThrow(() -> {
                log.error("Hospital not found with registration code: {}", registrationCode);
                return new ResourceNotFoundException("Hospital not found with registration code: " + registrationCode);
            });
    }

    @Override
    public Hospital createHospital(HospitalRequest request) {
        log.info("Creating hospital: {}", request.getName());

        // Keep hospital onboarding minimal by defaulting optional non-null fields.
        if (request.getNumberOfBed() == null) {
            request.setNumberOfBed(0);
        }
        if (request.getHospitalType() == null || request.getHospitalType().isBlank()) {
            request.setHospitalType(HospitalType.GENERAL.name());
        }
        if (request.getOrganizationType() == null || request.getOrganizationType().isBlank()) {
            request.setOrganizationType(OrganizationType.GOVERNMENT.name());
        }

        // Check for duplicate registration code if provided
        if (request.getRegistrationCode() != null &&
            !request.getRegistrationCode().isEmpty() &&
            hospitalRepository.existsByRegistrationCode(request.getRegistrationCode())) {
            throw new DuplicateResourceException(
                "Hospital with registration code " + request.getRegistrationCode() + " already exists"
            );
        }

        Hospital hospital = hospitalMapper.toEntity(request);

        // Handle tags
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            Set<Tag> tags = fetchTags(request.getTagIds());
            hospital.setTags(tags);
            log.info("Setting {} tags for hospital", tags.size());
        }

        Hospital savedHospital = hospitalRepository.save(hospital);
        log.info("Created hospital with id: {}", savedHospital.getId());
        return savedHospital;
    }

    @Override
    public Hospital updateHospital(Integer id, HospitalRequest request) {
        log.info("Updating hospital with id: {}", id);

        Hospital hospital = getHospitalById(id);

        // Check registration code uniqueness if changed
        if (request.getRegistrationCode() != null &&
            !request.getRegistrationCode().isEmpty() &&
            !request.getRegistrationCode().equals(hospital.getRegistrationCode())) {
            if (hospitalRepository.existsByRegistrationCode(request.getRegistrationCode())) {
                throw new DuplicateResourceException(
                    "Hospital with registration code " + request.getRegistrationCode() + " already exists"
                );
            }
        }

        // Handle tags update
        if (request.getTagIds() != null) {
            Set<Tag> tags = fetchTags(request.getTagIds());
            hospital.setTags(tags);
            log.info("Updating {} tags for hospital", tags.size());
        }

        hospitalMapper.partialUpdate(request, hospital);
        Hospital updatedHospital = hospitalRepository.save(hospital);
        log.info("Updated hospital with id: {}", updatedHospital.getId());
        return updatedHospital;
    }

    @Override
    public void deleteHospital(Integer id) {
        log.info("Deleting hospital with id: {}", id);

        // Verify hospital exists
        getHospitalById(id);
        checkHospitalDependencies(id);

        hospitalRepository.deleteById(id);

        if (hospitalRepository.findById(id).isPresent()) {
            throw new UnprocessableEntityException("Failed to delete hospital");
        }
    }

    @Override
    public void activateHospital(Integer id) {
        log.info("Activating hospital with id: {}", id);
        Hospital hospital = getHospitalById(id);
        hospital.setIsActive(true);
        hospitalRepository.save(hospital);
        log.info("Hospital activated with id: {}", id);
    }

    @Override
    public void deactivateHospital(Integer id) {
        log.info("Deactivating hospital with id: {}", id);
        Hospital hospital = getHospitalById(id);
        hospital.setIsActive(false);
        hospitalRepository.save(hospital);
        log.info("Hospital deactivated with id: {}", id);
    }

    private void validateId(Integer id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Hospital ID must be positive");
        }
    }

    private void checkHospitalDependencies(Integer id) {
        List<String> dependencies = new ArrayList<>();

        long medicalTestCount = hospitalMedicalTestRepository.countAllByHospital_Id(id);
        if (medicalTestCount > 0) {
            dependencies.add("Medical Test Count: " + medicalTestCount);
        }

        long doctorWorkplaceCount = doctorWorkplaceRepository.countAllByHospital_Id(id);
        if (doctorWorkplaceCount > 0) {
            dependencies.add("Doctor Workplace Count: " + doctorWorkplaceCount);
        }

        if (!dependencies.isEmpty()) {
            throw new UnprocessableEntityException(
                String.format(
                    "Hospital with ID %d cannot be deleted because it is used in the following tables: %s",
                    id, String.join(", ", dependencies)
                )
            );
        }
    }

    private Set<Tag> fetchTags(Set<Integer> tagIds) {
        Set<Tag> tags = new HashSet<>();
        for (Integer tagId : tagIds) {
            tagService.getTagById(tagId).ifPresent(tags::add);
        }
        return tags;
    }

    private List<Filter> generateQueryFilters(HospitalFilter filter) {
        List<Filter> filters = new ArrayList<>();

        if (filter.getName() != null) {
            filters.add(generateIndividualFilter("name", LIKE, filter.getName()));
        }

        if (filter.getBnName() != null) {
            filters.add(generateIndividualFilter("bnName", LIKE, filter.getBnName()));
        }

        if (filter.getNumberOfBed() != null) {
            filters.add(generateIndividualFilter("numberOfBed", EQUALS, filter.getNumberOfBed()));
        }

        if (filter.getDistrictId() != null) {
            filters.add(generateJoinTableFilter("id", "district", JOIN, filter.getDistrictId()));
        }

        if (filter.getUpazilaId() != null) {
            filters.add(generateJoinTableFilter("id", "upazila", JOIN, filter.getUpazilaId()));
        }

        if (filter.getUnionId() != null) {
            filters.add(generateJoinTableFilter("id", "union", JOIN, filter.getUnionId()));
        }

        if (filter.getHospitalTypes() != null && !filter.getHospitalTypes().isEmpty()) {
            List<HospitalType> hospitalTypes = new ArrayList<>();
            for (String hospitalType : filter.getHospitalTypes()) {
                try {
                    hospitalTypes.add(HospitalType.valueOf(hospitalType));
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid hospital type: {}", hospitalType);
                }
            }
            if (!hospitalTypes.isEmpty()) {
                filters.add(generateInFilter("hospitalType", IN, hospitalTypes));
            }
        }

        if (filter.getOrganizationType() != null) {
            try {
                OrganizationType orgType = OrganizationType.valueOf(filter.getOrganizationType());
                filters.add(generateIndividualFilter("organizationType", EQUALS, orgType));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid organization type: {}", filter.getOrganizationType());
            }
        }

        if (filter.getIsActive() != null) {
            filters.add(generateIndividualFilter("isActive", EQUALS, filter.getIsActive()));
        }

        if (filter.getHasEmergencyService() != null) {
            filters.add(generateIndividualFilter("hasEmergencyService", EQUALS, filter.getHasEmergencyService()));
        }

        if (filter.getHasAmbulanceService() != null) {
            filters.add(generateIndividualFilter("hasAmbulanceService", EQUALS, filter.getHasAmbulanceService()));
        }

        if (filter.getHasBloodBank() != null) {
            filters.add(generateIndividualFilter("hasBloodBank", EQUALS, filter.getHasBloodBank()));
        }

        return filters;
    }
}