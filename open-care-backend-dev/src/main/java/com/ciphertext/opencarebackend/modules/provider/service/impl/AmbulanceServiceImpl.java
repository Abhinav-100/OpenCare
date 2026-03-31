package com.ciphertext.opencarebackend.modules.provider.service.impl;
import com.ciphertext.opencarebackend.modules.provider.dto.filter.AmbulanceFilter;
import com.ciphertext.opencarebackend.entity.Ambulance;
import com.ciphertext.opencarebackend.entity.District;
import com.ciphertext.opencarebackend.entity.Hospital;
import com.ciphertext.opencarebackend.entity.Upazila;
import com.ciphertext.opencarebackend.enums.AmbulanceType;
import com.ciphertext.opencarebackend.exception.BadRequestException;
import com.ciphertext.opencarebackend.exception.DuplicateResourceException;
import com.ciphertext.opencarebackend.exception.ResourceNotFoundException;
import com.ciphertext.opencarebackend.modules.provider.repository.AmbulanceRepository;
import com.ciphertext.opencarebackend.modules.shared.repository.DistrictRepository;
import com.ciphertext.opencarebackend.modules.provider.repository.HospitalRepository;
import com.ciphertext.opencarebackend.modules.shared.repository.UpazilaRepository;
import com.ciphertext.opencarebackend.modules.shared.repository.specification.Filter;
import com.ciphertext.opencarebackend.modules.provider.service.AmbulanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.ciphertext.opencarebackend.modules.shared.repository.specification.QueryFilterUtils.generateIndividualFilter;
import static com.ciphertext.opencarebackend.modules.shared.repository.specification.QueryFilterUtils.generateJoinTableFilter;
import static com.ciphertext.opencarebackend.modules.shared.repository.specification.QueryOperator.*;
import static com.ciphertext.opencarebackend.modules.shared.repository.specification.SpecificationBuilder.createSpecification;
import static org.springframework.data.jpa.domain.Specification.where;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AmbulanceServiceImpl implements AmbulanceService {

    private final AmbulanceRepository ambulanceRepository;
    private final HospitalRepository hospitalRepository;
    private final DistrictRepository districtRepository;
    private final UpazilaRepository upazilaRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<Ambulance> getPaginatedDataWithFilters(AmbulanceFilter ambulanceFilter, Pageable pageable) {
        log.info("Fetching ambulances with filters: {}", ambulanceFilter);
        List<Filter> filterList = generateQueryFilters(ambulanceFilter);
        Specification<Ambulance> specification = where(null);
        if (!filterList.isEmpty()) {
            specification = where(createSpecification(filterList.removeFirst()));
            for (Filter input : filterList) {
                specification = specification.and(createSpecification(input));
            }
        }
        return ambulanceRepository.findAll(specification, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ambulance> getAllAmbulance() {
        return ambulanceRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Ambulance getAmbulanceById(Integer id) {
        validatePositiveId(id, "Ambulance");
        return ambulanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ambulance not found with id: " + id));
    }

    @Override
    public Ambulance createAmbulance(Ambulance ambulance) {
        if (ambulance == null) {
            throw new BadRequestException("Ambulance payload is required");
        }
        validateRequiredFields(ambulance);
        if (ambulanceRepository.existsByVehicleNumber(ambulance.getVehicleNumber())) {
            throw new DuplicateResourceException("Ambulance with vehicle number already exists");
        }
        hydrateReferences(ambulance);
        applyDefaults(ambulance);
        return ambulanceRepository.save(ambulance);
    }

    @Override
    public Ambulance updateAmbulanceById(Ambulance ambulance, Integer id) {
        if (ambulance == null) {
            throw new BadRequestException("Ambulance payload is required");
        }
        Ambulance existingAmbulance = getAmbulanceById(id);
        validateRequiredFields(ambulance);
        if (ambulanceRepository.existsByVehicleNumberAndIdNot(ambulance.getVehicleNumber(), id)) {
            throw new DuplicateResourceException("Ambulance with vehicle number already exists");
        }
        hydrateReferences(ambulance);
        applyDefaults(ambulance);

        existingAmbulance.setVehicleNumber(ambulance.getVehicleNumber());
        existingAmbulance.setType(ambulance.getType());
        existingAmbulance.setDriverName(ambulance.getDriverName());
        existingAmbulance.setDriverPhone(ambulance.getDriverPhone());
        existingAmbulance.setIsAvailable(ambulance.getIsAvailable());
        existingAmbulance.setIsAffiliated(ambulance.getIsAffiliated());
        existingAmbulance.setHospital(ambulance.getHospital());
        existingAmbulance.setDistrict(ambulance.getDistrict());
        existingAmbulance.setUpazila(ambulance.getUpazila());
        existingAmbulance.setIsActive(ambulance.getIsActive());
        return ambulanceRepository.save(existingAmbulance);
    }

    @Override
    public void deleteAmbulanceById(Integer id) {
        Ambulance existingAmbulance = getAmbulanceById(id);
        ambulanceRepository.delete(existingAmbulance);
    }

    private List<Filter> generateQueryFilters(AmbulanceFilter ambulanceFilter) {
        List<Filter> filters = new ArrayList<>();

        if (ambulanceFilter.getDriverPhone() != null) {
            filters.add(generateIndividualFilter("driverPhone", LIKE, ambulanceFilter.getDriverPhone()));
        }

        if (ambulanceFilter.getDriverName() != null) {
            filters.add(generateIndividualFilter("driverName", LIKE, ambulanceFilter.getDriverName()));
        }

        if (ambulanceFilter.getVehicleNumber() != null) {
            filters.add(generateIndividualFilter("vehicleNumber", LIKE, ambulanceFilter.getVehicleNumber()));
        }

        if (ambulanceFilter.getDistrictId() != null) {
            filters.add(generateJoinTableFilter("id", "district", JOIN, ambulanceFilter.getDistrictId()));
        }

        if (ambulanceFilter.getUpazilaId() != null) {
            filters.add(generateJoinTableFilter("id", "upazila", JOIN, ambulanceFilter.getUpazilaId()));
        }

        if (ambulanceFilter.getHospitalId() != null) {
            filters.add(generateJoinTableFilter("id", "hospital", JOIN, ambulanceFilter.getHospitalId()));
        }

        if (StringUtils.hasText(ambulanceFilter.getType())) {
            filters.add(generateIndividualFilter("type", EQUALS, parseType(ambulanceFilter.getType())));
        }

        if (ambulanceFilter.getIsAvailable() != null) {
            filters.add(generateIndividualFilter("isAvailable", EQUALS, ambulanceFilter.getIsAvailable()));
        }

        if (ambulanceFilter.getIsAffiliated() != null) {
            filters.add(generateIndividualFilter("isAffiliated", EQUALS, ambulanceFilter.getIsAffiliated()));
        }

        if (ambulanceFilter.getIsActive() != null) {
            filters.add(generateIndividualFilter("isActive", EQUALS, ambulanceFilter.getIsActive()));
        }

        return filters;
    }

    private void hydrateReferences(Ambulance ambulance) {
        Integer hospitalId = ambulance.getHospital() != null ? ambulance.getHospital().getId() : null;
        Integer districtId = ambulance.getDistrict() != null ? ambulance.getDistrict().getId() : null;
        Integer upazilaId = ambulance.getUpazila() != null ? ambulance.getUpazila().getId() : null;

        if (hospitalId != null) {
            validatePositiveId(hospitalId, "Hospital");
            Hospital hospital = hospitalRepository.findById(hospitalId)
                    .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with id: " + hospitalId));
            ambulance.setHospital(hospital);
        }

        if (districtId != null) {
            validatePositiveId(districtId, "District");
            District district = districtRepository.findById(districtId)
                    .orElseThrow(() -> new ResourceNotFoundException("District not found with id: " + districtId));
            ambulance.setDistrict(district);
        }

        if (upazilaId != null) {
            validatePositiveId(upazilaId, "Upazila");
            Upazila upazila = upazilaRepository.findById(upazilaId)
                    .orElseThrow(() -> new ResourceNotFoundException("Upazila not found with id: " + upazilaId));
            ambulance.setUpazila(upazila);
        }
    }

    private void applyDefaults(Ambulance ambulance) {
        if (ambulance.getIsAvailable() == null) {
            ambulance.setIsAvailable(true);
        }
        if (ambulance.getIsAffiliated() == null) {
            ambulance.setIsAffiliated(false);
        }
        if (ambulance.getIsActive() == null) {
            ambulance.setIsActive(true);
        }
    }

    private void validateRequiredFields(Ambulance ambulance) {
        if (!StringUtils.hasText(ambulance.getVehicleNumber())) {
            throw new BadRequestException("Vehicle number is required");
        }
    }

    private AmbulanceType parseType(String type) {
        try {
            return AmbulanceType.valueOf(type);
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Invalid ambulance type");
        }
    }

    private void validatePositiveId(Integer id, String name) {
        if (id == null || id <= 0) {
            throw new BadRequestException(name + " ID must be positive");
        }
    }
}
