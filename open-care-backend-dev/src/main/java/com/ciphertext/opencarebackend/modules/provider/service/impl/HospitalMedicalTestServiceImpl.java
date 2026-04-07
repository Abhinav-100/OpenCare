package com.ciphertext.opencarebackend.modules.provider.service.impl;
import com.ciphertext.opencarebackend.modules.provider.dto.filter.MedicalTestFilter;
import com.ciphertext.opencarebackend.entity.HospitalMedicalTest;
import com.ciphertext.opencarebackend.exception.BadRequestException;
import com.ciphertext.opencarebackend.exception.ResourceNotFoundException;
import com.ciphertext.opencarebackend.modules.provider.repository.HospitalMedicalTestRepository;
import com.ciphertext.opencarebackend.modules.shared.repository.specification.Filter;
import com.ciphertext.opencarebackend.modules.provider.service.HospitalMedicalTestService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.ciphertext.opencarebackend.modules.shared.repository.specification.QueryFilterUtils.generateIndividualFilter;
import static com.ciphertext.opencarebackend.modules.shared.repository.specification.QueryFilterUtils.generateJoinTableFilter;
import static com.ciphertext.opencarebackend.modules.shared.repository.specification.QueryOperator.*;
import static com.ciphertext.opencarebackend.modules.shared.repository.specification.SpecificationBuilder.createSpecification;
import static org.springframework.data.jpa.domain.Specification.where;
@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
/**
 * Flow note: HospitalMedicalTestServiceImpl belongs to the provider doctor/hospital module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public class HospitalMedicalTestServiceImpl implements HospitalMedicalTestService {
    private final HospitalMedicalTestRepository hospitalMedicalTestRepository;

    @Override
    @Transactional(readOnly = true)
    public List<HospitalMedicalTest> getMedicalTestsByHospitalId(Long hospitalId) {
        if (hospitalId == null || hospitalId <= 0) {
            throw new BadRequestException("Hospital ID must be positive");
        }
        log.info("Fetching all medical tests for hospital ID: {}", hospitalId);
        List<HospitalMedicalTest> medicalTests = hospitalMedicalTestRepository.findByHospitalId(hospitalId);
        log.info("Retrieved {} hospital medical tests", medicalTests.size());
        return medicalTests;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HospitalMedicalTest> getPaginatedDataWithFilters(MedicalTestFilter medicalTestFilter, Pageable pagingSort) {
        log.info("Fetching hospital medical tests with filters: {}", medicalTestFilter);
        List<Filter> filterList = generateQueryFilters(medicalTestFilter);

        Specification<HospitalMedicalTest> specification = where(null);
        if (!filterList.isEmpty()) {
            specification = where(createSpecification(filterList.removeFirst()));
            for (Filter input : filterList) {
                specification = specification.and(createSpecification(input));
            }
        }

        return hospitalMedicalTestRepository.findAll(specification, pagingSort);
    }

    @Override
    @Transactional(readOnly = true)
    public HospitalMedicalTest getMedicalTestById(Long id) throws ResourceNotFoundException {
        if (id == null || id <= 0) {
            log.error("Invalid hospital medical test ID: {}", id);
            throw new BadRequestException("Medical test ID must be positive");
        }

        log.info("Fetching hospital medical test with id: {}", id);
        return hospitalMedicalTestRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Hospital medical test not found with id: {}", id);
                    return new ResourceNotFoundException("Hospital medical test not found with id: " + id);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public HospitalMedicalTest getMedicalTestByIdAndHospitalId(Long hospitalId, Long id) {
        if (hospitalId == null || hospitalId <= 0) {
            throw new BadRequestException("Hospital ID must be positive");
        }
        if (id == null || id <= 0) {
            throw new BadRequestException("Medical test ID must be positive");
        }

        log.info("Fetching hospital medical test with id: {} for hospitalId: {}", id, hospitalId);
        return hospitalMedicalTestRepository.findByIdAndHospitalId(id, hospitalId)
                .orElseThrow(() -> {
                    log.error("Hospital medical test not found with id: {} for hospitalId: {}", id, hospitalId);
                    return new ResourceNotFoundException("Hospital medical test not found with id: " + id);
                });
    }

    @Override
    public HospitalMedicalTest createMedicalTest(HospitalMedicalTest hospitalMedicalTest) {
        log.info("Creating hospital medical test: {}", hospitalMedicalTest);
        if (hospitalMedicalTest.getIsActive() == null) {
            hospitalMedicalTest.setIsActive(true);
        }
        return hospitalMedicalTestRepository.save(hospitalMedicalTest);
    }

    @Override
    public HospitalMedicalTest updateMedicalTest(HospitalMedicalTest newMedicalTest, Long medicalTestId) {
        log.info("Updating hospital medical test: {}", newMedicalTest);
        HospitalMedicalTest medicalTest = getMedicalTestById(medicalTestId);
        if (newMedicalTest.getHospital() != null) {
            medicalTest.setHospital(newMedicalTest.getHospital());
        }
        medicalTest.setName(newMedicalTest.getName());
        medicalTest.setTestCode(newMedicalTest.getTestCode());
        medicalTest.setCategory(newMedicalTest.getCategory());
        medicalTest.setDescription(newMedicalTest.getDescription());
        medicalTest.setPrice(newMedicalTest.getPrice());
        medicalTest.setIsAvailable(newMedicalTest.getIsAvailable());
        medicalTest.setDuration(newMedicalTest.getDuration());
        medicalTest.setProcessingTimeMinutes(newMedicalTest.getProcessingTimeMinutes());
        medicalTest.setSampleCollectedTime(newMedicalTest.getSampleCollectedTime());
        medicalTest.setDeliveryTime(newMedicalTest.getDeliveryTime());
        if (newMedicalTest.getMedicalTestId() != null) {
            medicalTest.setMedicalTestId(newMedicalTest.getMedicalTestId());
        }
        if (newMedicalTest.getIsActive() != null) {
            medicalTest.setIsActive(newMedicalTest.getIsActive());
        }
        return hospitalMedicalTestRepository.save(medicalTest);
    }

    @Override
    public void deleteMedicalTestById(Long medicalTestId) {
        log.info("Deleting hospital medical test with id: {}", medicalTestId);
        if (medicalTestId == null || medicalTestId <= 0) {
            throw new BadRequestException("Medical test ID must be positive");
        }
        HospitalMedicalTest existing = getMedicalTestById(medicalTestId);
        hospitalMedicalTestRepository.delete(existing);
    }

    public List<Filter> generateQueryFilters(MedicalTestFilter medicalTestFilter) {
        List<Filter> filters = new ArrayList<>();

        if (medicalTestFilter.getName() != null && !medicalTestFilter.getName().isEmpty()) {
            filters.add(generateIndividualFilter("name", LIKE, medicalTestFilter.getName()));
        }

        if (medicalTestFilter.getHospitalId() != null) {
            filters.add(generateJoinTableFilter("id", "hospital", JOIN, medicalTestFilter.getHospitalId()));
        }

        if(medicalTestFilter.getMedicalTestId() != null) {
            filters.add(generateJoinTableFilter("id", "medicalTest", JOIN, medicalTestFilter.getMedicalTestId()));
        }

        if(medicalTestFilter.getParentMedicalTestId() != null) {
            filters.add(generateJoinTableFilter("parentId", "medicalTest", JOIN, medicalTestFilter.getParentMedicalTestId()));
        }

        if (medicalTestFilter.getMinPrice() != null) {
            filters.add(generateIndividualFilter("price", GREATER_THAN_EQUALS, medicalTestFilter.getMinPrice()));
        }

        if (medicalTestFilter.getMaxPrice() != null) {
            filters.add(generateIndividualFilter("price", LESS_THAN_EQUALS, medicalTestFilter.getMaxPrice()));
        }

        if (medicalTestFilter.getAvailable() != null) {
            filters.add(generateIndividualFilter("isAvailable", EQUALS, medicalTestFilter.getAvailable() > 0));
        }

        if (medicalTestFilter.getCategory() != null && !medicalTestFilter.getCategory().isEmpty()) {
            filters.add(generateIndividualFilter("category", LIKE, medicalTestFilter.getCategory()));
        }

        return filters;
    }
}