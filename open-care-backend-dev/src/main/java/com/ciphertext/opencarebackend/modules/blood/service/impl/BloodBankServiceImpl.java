package com.ciphertext.opencarebackend.modules.blood.service.impl;
import com.ciphertext.opencarebackend.modules.blood.dto.filter.BloodBankFilter;
import com.ciphertext.opencarebackend.entity.BloodBank;
import com.ciphertext.opencarebackend.entity.BloodInventory;
import com.ciphertext.opencarebackend.entity.Hospital;
import com.ciphertext.opencarebackend.exception.BadRequestException;
import com.ciphertext.opencarebackend.exception.ResourceNotFoundException;
import com.ciphertext.opencarebackend.modules.blood.repository.BloodBankRepository;
import com.ciphertext.opencarebackend.modules.blood.repository.BloodInventoryRepository;
import com.ciphertext.opencarebackend.modules.provider.repository.HospitalRepository;
import com.ciphertext.opencarebackend.modules.shared.repository.specification.Filter;
import com.ciphertext.opencarebackend.modules.blood.service.BloodBankService;
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
public class BloodBankServiceImpl implements BloodBankService {

    private final BloodBankRepository bloodBankRepository;
    private final BloodInventoryRepository bloodInventoryRepository;
    private final HospitalRepository hospitalRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<BloodBank> getPaginatedDataWithFilters(BloodBankFilter filter, Pageable pageable) {
        log.info("Fetching blood banks with filters: {}", filter);
        List<Filter> filterList = generateQueryFilters(filter);
        Specification<BloodBank> specification = where(null);
        if (!filterList.isEmpty()) {
            specification = where(createSpecification(filterList.removeFirst()));
            for (Filter input : filterList) {
                specification = specification.and(createSpecification(input));
            }
        }
        return bloodBankRepository.findAll(specification, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BloodBank> getAllActiveBloodBanks() {
        return bloodBankRepository.findByIsActiveTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public BloodBank getBloodBankById(Integer id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Blood Bank ID must be positive");
        }
        return bloodBankRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blood Bank not found with id: " + id));
    }

    @Override
    public BloodBank createBloodBank(BloodBank bloodBank) {
        if (bloodBank == null) {
            throw new BadRequestException("Blood Bank payload is required");
        }
        validateRequiredFields(bloodBank);
        hydrateReferences(bloodBank);
        applyDefaults(bloodBank);
        return bloodBankRepository.save(bloodBank);
    }

    @Override
    public BloodBank updateBloodBank(BloodBank bloodBank, Integer id) {
        if (bloodBank == null) {
            throw new BadRequestException("Blood Bank payload is required");
        }
        BloodBank existing = getBloodBankById(id);
        hydrateReferences(bloodBank);

        existing.setName(bloodBank.getName());
        existing.setLicenseNo(bloodBank.getLicenseNo());
        existing.setContactNumber(bloodBank.getContactNumber());
        existing.setEmail(bloodBank.getEmail());
        existing.setAddress(bloodBank.getAddress());
        existing.setLatitude(bloodBank.getLatitude());
        existing.setLongitude(bloodBank.getLongitude());
        existing.setOpeningHours(bloodBank.getOpeningHours());
        existing.setIsAlwaysOpen(bloodBank.getIsAlwaysOpen());
        existing.setIsActive(bloodBank.getIsActive());
        existing.setEstablishedDate(bloodBank.getEstablishedDate());
        existing.setHospital(bloodBank.getHospital());
        existing.setSocialOrganization(bloodBank.getSocialOrganization());

        return bloodBankRepository.save(existing);
    }

    @Override
    public void deleteBloodBankById(Integer id) {
        BloodBank bloodBank = getBloodBankById(id);
        bloodBankRepository.delete(bloodBank);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BloodInventory> getBloodBankInventory(Integer bloodBankId) {
        getBloodBankById(bloodBankId); // Verify blood bank exists
        return bloodInventoryRepository.findByBloodBankId(bloodBankId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BloodInventory> getAvailableInventory(Integer bloodBankId) {
        getBloodBankById(bloodBankId); // Verify blood bank exists
        return bloodInventoryRepository.findByBloodBankIdAndAvailableUnitsGreaterThan(bloodBankId, 0);
    }

    private List<Filter> generateQueryFilters(BloodBankFilter filter) {
        List<Filter> filters = new ArrayList<>();

        if (StringUtils.hasText(filter.getName())) {
            filters.add(generateIndividualFilter("name", LIKE, filter.getName()));
        }

        if (filter.getHospitalId() != null) {
            filters.add(generateJoinTableFilter("id", "hospital", JOIN, filter.getHospitalId()));
        }

        if (filter.getIsAlwaysOpen() != null) {
            filters.add(generateIndividualFilter("isAlwaysOpen", EQUALS, filter.getIsAlwaysOpen()));
        }

        if (filter.getIsActive() != null) {
            filters.add(generateIndividualFilter("isActive", EQUALS, filter.getIsActive()));
        }

        return filters;
    }

    private void hydrateReferences(BloodBank bloodBank) {
        Integer hospitalId = bloodBank.getHospital() != null ? bloodBank.getHospital().getId() : null;

        if (hospitalId != null) {
            Hospital hospital = hospitalRepository.findById(hospitalId)
                    .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with id: " + hospitalId));
            bloodBank.setHospital(hospital);
        }
    }

    private void validateRequiredFields(BloodBank bloodBank) {
        if (!StringUtils.hasText(bloodBank.getName())) {
            throw new BadRequestException("Blood bank name is required");
        }
    }

    private void applyDefaults(BloodBank bloodBank) {
        if (bloodBank.getIsActive() == null) {
            bloodBank.setIsActive(true);
        }
        if (bloodBank.getIsAlwaysOpen() == null) {
            bloodBank.setIsAlwaysOpen(false);
        }
    }
}
