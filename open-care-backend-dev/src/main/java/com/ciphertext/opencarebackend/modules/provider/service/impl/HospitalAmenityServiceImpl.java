package com.ciphertext.opencarebackend.modules.provider.service.impl;
import com.ciphertext.opencarebackend.modules.provider.dto.filter.AmenityFilter;
import com.ciphertext.opencarebackend.entity.HospitalAmenity;
import com.ciphertext.opencarebackend.exception.BadRequestException;
import com.ciphertext.opencarebackend.exception.ResourceNotFoundException;
import com.ciphertext.opencarebackend.modules.provider.repository.HospitalAmenityRepository;
import com.ciphertext.opencarebackend.modules.shared.repository.specification.Filter;
import com.ciphertext.opencarebackend.modules.provider.service.HospitalAmenityService;
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
 * Flow note: HospitalAmenityServiceImpl belongs to the provider doctor/hospital module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public class HospitalAmenityServiceImpl implements HospitalAmenityService {
    private final HospitalAmenityRepository hospitalAmenityRepository;

    @Override
    @Transactional(readOnly = true)
    public List<HospitalAmenity> getHospitalAmenitiesByHospitalId(Long hospitalId) {
        if (hospitalId == null || hospitalId <= 0) {
            throw new BadRequestException("Hospital ID must be positive");
        }
        log.info("Fetching all hospital amenities for hospital ID: {}", hospitalId);
        List<HospitalAmenity> hospitalAmenities = hospitalAmenityRepository.findByHospitalId(hospitalId);
        log.info("Retrieved {} hospital amenities", hospitalAmenities.size());
        return hospitalAmenities;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HospitalAmenity> getPaginatedDataWithFilters(AmenityFilter amenityFilter, Pageable pagingSort) {
        log.info("Fetching hospitals with filters: {}", amenityFilter);
        List<Filter> filterList = generateQueryFilters(amenityFilter);
        Specification<HospitalAmenity> specification = where(null);
        if(!filterList.isEmpty()) {
            specification = where(createSpecification(filterList.removeFirst()));
            for (Filter input : filterList) {
                specification = specification.and(createSpecification(input));
            }
        }
        log.info("Fetching hospitals with filters: {}", amenityFilter);
        return hospitalAmenityRepository.findAll(specification, pagingSort);
    }

    @Override
    @Transactional(readOnly = true)
    public HospitalAmenity getHospitalAmenityById(Long id) throws ResourceNotFoundException {
        if (id == null || id <= 0) {
            log.error("Invalid hospital amenity ID: {}", id);
            throw new BadRequestException("Hospital amenity ID must be positive");
        }

        log.info("Fetching hospital amenity with id: {}", id);
        return hospitalAmenityRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Hospital amenity not found with id: {}", id);
                    return new ResourceNotFoundException("Hospital amenity not found with id: " + id);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public HospitalAmenity getHospitalAmenityByIdAndHospitalId(Long hospitalId, Long id) throws ResourceNotFoundException {
        if (hospitalId == null || hospitalId <= 0) {
            throw new BadRequestException("Hospital ID must be positive");
        }
        if (id == null || id <= 0) {
            throw new BadRequestException("Hospital amenity ID must be positive");
        }

        log.info("Fetching hospital amenity with id: {} for hospitalId: {}", id, hospitalId);
        return hospitalAmenityRepository.findByIdAndHospitalId(id, hospitalId)
                .orElseThrow(() -> {
                    log.error("Hospital amenity not found with id: {} for hospitalId: {}", id, hospitalId);
                    return new ResourceNotFoundException("Hospital amenity not found with id: " + id);
                });
    }

    @Override
    public HospitalAmenity createHospitalAmenity(HospitalAmenity hospitalAmenity) {
        log.info("Creating hospital amenity: {}", hospitalAmenity);
        if (hospitalAmenity.getIsActive() == null) {
            hospitalAmenity.setIsActive(true);
        }
        return hospitalAmenityRepository.save(hospitalAmenity);
    }

    @Override
    public HospitalAmenity updateHospitalAmenity(HospitalAmenity newHospitalAmenity, Long hospitalAmenityId) {
        log.info("Updating hospital amenity: {}", newHospitalAmenity);
        HospitalAmenity hospitalAmenity = getHospitalAmenityById(hospitalAmenityId);
        if (newHospitalAmenity.getHospital() != null) {
            hospitalAmenity.setHospital(newHospitalAmenity.getHospital());
        }
        hospitalAmenity.setType(newHospitalAmenity.getType());
        hospitalAmenity.setName(newHospitalAmenity.getName());
        hospitalAmenity.setPrice(newHospitalAmenity.getPrice());
        hospitalAmenity.setQuantity(newHospitalAmenity.getQuantity());
        hospitalAmenity.setAvailable(newHospitalAmenity.getAvailable());
        if (newHospitalAmenity.getIsActive() != null) {
            hospitalAmenity.setIsActive(newHospitalAmenity.getIsActive());
        }
        return hospitalAmenityRepository.save(hospitalAmenity);
    }

    @Override
    public void deleteHospitalAmenityById(Long hospitalAmenityId) {
        log.info("Deleting hospital amenity with id: {}", hospitalAmenityId);
        if (hospitalAmenityId == null || hospitalAmenityId <= 0) {
            throw new BadRequestException("Hospital amenity ID must be positive");
        }
        HospitalAmenity existing = getHospitalAmenityById(hospitalAmenityId);
        hospitalAmenityRepository.delete(existing);
    }

    public List<Filter> generateQueryFilters(AmenityFilter amenityFilter) {

        List<Filter> filters = new ArrayList<>();

        if(amenityFilter.getName() != null && !amenityFilter.getName().isEmpty()) {
            filters.add(generateIndividualFilter("name", LIKE, amenityFilter.getName()));
        }

        if(amenityFilter.getHospitalId() != null) {
            filters.add(generateJoinTableFilter("id", "hospital", JOIN, amenityFilter.getHospitalId()));
        }

        if(amenityFilter.getType() != null) {
            filters.add(generateIndividualFilter("type", LIKE, amenityFilter.getType()));
        }

        if(amenityFilter.getMinPrice() != null) {
            filters.add(generateIndividualFilter("price", GREATER_THAN_EQUALS, amenityFilter.getMinPrice()));
        }

        if(amenityFilter.getMaxPrice() != null) {
            filters.add(generateIndividualFilter("price", LESS_THAN_EQUALS, amenityFilter.getMaxPrice()));
        }

        if(amenityFilter.getAvailable() != null) {
            filters.add(generateIndividualFilter("available", EQUALS, amenityFilter.getAvailable()));
        }

        return filters;
    }
}