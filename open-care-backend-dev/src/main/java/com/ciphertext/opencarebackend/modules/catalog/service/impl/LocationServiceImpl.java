package com.ciphertext.opencarebackend.modules.catalog.service.impl;

import com.ciphertext.opencarebackend.entity.District;
import com.ciphertext.opencarebackend.entity.Division;
import com.ciphertext.opencarebackend.entity.Union;
import com.ciphertext.opencarebackend.entity.Upazila;
import com.ciphertext.opencarebackend.exception.BadRequestException;
import com.ciphertext.opencarebackend.exception.ResourceNotFoundException;
import com.ciphertext.opencarebackend.modules.shared.repository.DistrictRepository;
import com.ciphertext.opencarebackend.modules.shared.repository.DivisionRepository;
import com.ciphertext.opencarebackend.modules.shared.repository.UnionRepository;
import com.ciphertext.opencarebackend.modules.shared.repository.UpazilaRepository;
import com.ciphertext.opencarebackend.modules.catalog.service.LocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of LocationService for managing geographic locations
 * including divisions, districts, upazilas, and unions
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final DivisionRepository divisionRepository;
    private final DistrictRepository districtRepository;
    private final UpazilaRepository upazilaRepository;
    private final UnionRepository unionRepository;

    @Override
    @Cacheable("divisions")
    public List<Division> getAllDivisions() {
        log.info("Fetching all divisions");
        List<Division> divisions = divisionRepository.findAll();
        validateResultNotEmpty(divisions, "No divisions found");
        log.info("Retrieved {} divisions", divisions.size());
        return divisions;
    }

    @Override
    @Cacheable(value = "divisions", key = "#id")
    public Division getDivisionById(int id) throws ResourceNotFoundException {
        validatePositiveId(id, "Division ID must be positive");

        log.info("Fetching division with id: {}", id);
        return divisionRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Division not found with id: {}", id);
                    return new ResourceNotFoundException("Division not found with id: " + id);
                });
    }

    @Override
    @Cacheable(value = "districts")
    public List<District> getAllDistricts() {
        log.info("Fetching all districts");
        List<District> districts = districtRepository.findAll();
        validateResultNotEmpty(districts, "No districts found");
        log.info("Retrieved {} districts", districts.size());
        return districts;
    }

    @Override
    @Cacheable(value = "districts", key = "#id")
    public District getDistrictById(int id) throws ResourceNotFoundException {
        validatePositiveId(id, "District ID must be positive");

        log.info("Fetching district with id: {}", id);
        return districtRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("District not found with id: {}", id);
                    return new ResourceNotFoundException("District not found with id: " + id);
                });
    }

    @Override
    @Cacheable(value = "districtsByDivision", key = "#divisionId")
    public List<District> getAllDistrictsByDivisionId(int divisionId) throws ResourceNotFoundException {
        validatePositiveId(divisionId, "Division ID must be positive");

        log.info("Fetching all districts for division id: {}", divisionId);
        Division division = getDivisionById(divisionId);
        List<District> districts = districtRepository.getAllByDivision(division);
        validateResultNotEmpty(districts, "No districts found for division id: " + divisionId);
        log.info("Retrieved {} districts for division id: {}", districts.size(), divisionId);
        return districts;
    }

    @Override
    @Cacheable(value = "upazilas")
    public List<Upazila> getAllUpazilas() {
        log.info("Fetching all upazilas");
        List<Upazila> upazilas = upazilaRepository.findAll();
        validateResultNotEmpty(upazilas, "No upazilas found");
        log.info("Retrieved {} upazilas", upazilas.size());
        return upazilas;
    }

    @Override
    @Cacheable(value = "upazilas", key = "#id")
    public Upazila getUpazilaById(int id) throws ResourceNotFoundException {
        validatePositiveId(id, "Upazila ID must be positive");

        log.info("Fetching upazila with id: {}", id);
        return upazilaRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Upazila not found with id: {}", id);
                    return new ResourceNotFoundException("Upazila not found with id: " + id);
                });
    }

    @Override
    @Cacheable(value = "upazilasByDistrict", key = "#districtId")
    public List<Upazila> getAllUpazilasByDistrictId(int districtId) throws ResourceNotFoundException {
        validatePositiveId(districtId, "District ID must be positive");

        log.info("Fetching all upazilas for district id: {}", districtId);
        District district = getDistrictById(districtId);
        List<Upazila> upazilas = upazilaRepository.getAllByDistrict(district);
        validateResultNotEmpty(upazilas, "No upazilas found for district id: " + districtId);
        log.info("Retrieved {} upazilas for district id: {}", upazilas.size(), districtId);
        return upazilas;
    }

    @Override
    @Cacheable(value = "unionsByUpazila", key = "#upazilaId")
    public List<Union> getAllUnionsByUpazilaId(int upazilaId) throws ResourceNotFoundException {
        validatePositiveId(upazilaId, "Upazila ID must be positive");

        log.info("Fetching all unions for upazila id: {}", upazilaId);
        Upazila upazila = getUpazilaById(upazilaId);
        List<Union> unions = unionRepository.getAllByUpazila(upazila);
        validateResultNotEmpty(unions, "No unions found for upazila id: " + upazilaId);
        log.info("Retrieved {} unions for upazila id: {}", unions.size(), upazilaId);
        return unions;
    }

    /**
     * Helper method to validate that an ID is positive
     * @param id the ID to validate
     * @param message the error message
     * @throws BadRequestException if the ID is not positive
     */
    private void validatePositiveId(int id, String message) throws BadRequestException {
        if (id <= 0) {
            log.error("Invalid ID: {}", id);
            throw new BadRequestException(message);
        }
    }

    /**
     * Helper method to check if a collection is empty and throw an appropriate exception
     * @param collection the collection to check
     * @param message the error message
     * @throws ResourceNotFoundException if the collection is empty
     */
    private <T> void validateResultNotEmpty(List<T> collection, String message) throws ResourceNotFoundException {
        if (collection == null || collection.isEmpty()) {
            log.warn(message);
            throw new ResourceNotFoundException(message);
        }
    }
}
