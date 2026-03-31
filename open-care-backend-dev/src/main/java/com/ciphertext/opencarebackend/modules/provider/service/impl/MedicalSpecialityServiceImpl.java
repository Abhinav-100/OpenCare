package com.ciphertext.opencarebackend.modules.provider.service.impl;
import com.ciphertext.opencarebackend.modules.provider.dto.filter.MedicalSpecialityFilter;
import com.ciphertext.opencarebackend.modules.provider.dto.request.MedicalSpecialityRequest;
import com.ciphertext.opencarebackend.entity.MedicalSpeciality;
import com.ciphertext.opencarebackend.exception.BadRequestException;
import com.ciphertext.opencarebackend.exception.ResourceNotFoundException;
import com.ciphertext.opencarebackend.modules.provider.repository.MedicalSpecialityRepository;
import com.ciphertext.opencarebackend.modules.provider.service.MedicalSpecialityService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of MedicalSpecialityService for managing medical specialities for doctors
 */

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MedicalSpecialityServiceImpl implements MedicalSpecialityService {
    
    private final MedicalSpecialityRepository medicalSpecialityRepository;

    @Override
    public List<MedicalSpeciality> getAllSpecialities() {
        log.info("Fetching all medical specialities");
        List<MedicalSpeciality> medicalSpecialities = medicalSpecialityRepository.findAll();
        log.info("Retrieved {} medical specialities", medicalSpecialities.size());
        return medicalSpecialities;
    }

    @Override
    public MedicalSpeciality getSpecialityById(int id) throws ResourceNotFoundException {
        if (id <= 0) {
            log.error("Invalid medical speciality ID: {}", id);
            throw new BadRequestException("Medical Speciality ID must be positive");
        }

        log.info("Fetching medical speciality with id: {}", id);
        return medicalSpecialityRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Medical Speciality not found with id: {}", id);
                    return new ResourceNotFoundException("Medical Speciality not found with id: " + id);
                });
    }

    @Override
    public Page<MedicalSpeciality> getPaginatedDataWithFilters(MedicalSpecialityFilter filter, Pageable pageable) {
        log.info("Fetching paginated medical specialities with filters: {}", filter);

        Specification<MedicalSpeciality> specification = createSpecification(filter);
        Page<MedicalSpeciality> result = medicalSpecialityRepository.findAll(specification, pageable);

        log.info("Retrieved {} medical specialities from page {} of {}",
                result.getNumberOfElements(), result.getNumber(), result.getTotalPages());
        return result;
    }

    @Override
    @Transactional
    public MedicalSpeciality createSpeciality(MedicalSpeciality medicalSpeciality) {
        log.info("Creating new medical speciality: {}", medicalSpeciality.getName());

        if (medicalSpeciality.getDoctorCount() == null) {
            medicalSpeciality.setDoctorCount(0);
        }

        MedicalSpeciality savedSpeciality = medicalSpecialityRepository.save(medicalSpeciality);
        log.info("Created medical speciality with ID: {}", savedSpeciality.getId());
        return savedSpeciality;
    }

    @Override
    @Transactional
    public MedicalSpeciality updateSpeciality(MedicalSpecialityRequest request, int id) throws ResourceNotFoundException {
        log.info("Updating medical speciality with ID: {}", id);

        MedicalSpeciality existingSpeciality = getSpecialityById(id);

        // Update fields
        if (request.getName() != null) {
            existingSpeciality.setName(request.getName());
        }
        if (request.getBnName() != null) {
            existingSpeciality.setBnName(request.getBnName());
        }
        if (request.getParentId() != null) {
            existingSpeciality.setParentId(request.getParentId());
        }
        if (request.getIcon() != null) {
            existingSpeciality.setIcon(request.getIcon());
        }
        if (request.getImageUrl() != null) {
            existingSpeciality.setImageUrl(request.getImageUrl());
        }
        if (request.getDescription() != null) {
            existingSpeciality.setDescription(request.getDescription());
        }
        if (request.getDoctorCount() != null) {
            existingSpeciality.setDoctorCount(request.getDoctorCount());
        }

        MedicalSpeciality updatedSpeciality = medicalSpecialityRepository.save(existingSpeciality);
        log.info("Updated medical speciality with ID: {}", updatedSpeciality.getId());
        return updatedSpeciality;
    }

    @Override
    @Transactional
    public void deleteSpeciality(int id) throws ResourceNotFoundException {
        log.info("Deleting medical speciality with ID: {}", id);

        MedicalSpeciality existingSpeciality = getSpecialityById(id);
        medicalSpecialityRepository.delete(existingSpeciality);

        log.info("Deleted medical speciality with ID: {}", id);
    }

    private Specification<MedicalSpeciality> createSpecification(MedicalSpecialityFilter filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getName() != null && !filter.getName().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + filter.getName().toLowerCase() + "%"
                ));
            }

            if (filter.getBnName() != null && !filter.getBnName().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("bnName")),
                        "%" + filter.getBnName().toLowerCase() + "%"
                ));
            }

            if (filter.getParentId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("parentId"), filter.getParentId()));
            }

            if (filter.getDescription() != null && !filter.getDescription().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("description")),
                        "%" + filter.getDescription().toLowerCase() + "%"
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Transactional
    public void refreshAll() {
        medicalSpecialityRepository.refreshDoctorCounts();
    }

    @Override
    @Transactional
    public void refreshOne(int id) {
        medicalSpecialityRepository.refreshDoctorCountById(id);
    }
}
