package com.ciphertext.opencarebackend.modules.blood.service.impl;
import com.ciphertext.opencarebackend.modules.blood.dto.filter.BloodDonationFilter;
import com.ciphertext.opencarebackend.modules.blood.dto.response.BloodDonationResponse;
import com.ciphertext.opencarebackend.entity.BloodDonation;
import com.ciphertext.opencarebackend.entity.Hospital;
import com.ciphertext.opencarebackend.entity.Profile;
import com.ciphertext.opencarebackend.enums.BloodComponent;
import com.ciphertext.opencarebackend.exception.BadRequestException;
import com.ciphertext.opencarebackend.exception.ResourceNotFoundException;
import com.ciphertext.opencarebackend.mapper.BloodDonationMapper;
import com.ciphertext.opencarebackend.modules.blood.repository.BloodDonationRepository;
import com.ciphertext.opencarebackend.modules.provider.repository.HospitalRepository;
import com.ciphertext.opencarebackend.modules.user.repository.ProfileRepository;
import com.ciphertext.opencarebackend.modules.shared.repository.specification.Filter;
import com.ciphertext.opencarebackend.modules.blood.service.BloodDonationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.ciphertext.opencarebackend.modules.shared.repository.specification.QueryFilterUtils.*;
import static com.ciphertext.opencarebackend.modules.shared.repository.specification.QueryOperator.*;
import static com.ciphertext.opencarebackend.modules.shared.repository.specification.SpecificationBuilder.createSpecification;
import static org.springframework.data.jpa.domain.Specification.where;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BloodDonationServiceImpl implements BloodDonationService {

    private final BloodDonationRepository bloodDonationRepository;
    private final BloodDonationMapper bloodDonationMapper;
    private final ProfileRepository profileRepository;
    private final HospitalRepository hospitalRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<BloodDonation> getPaginatedDataWithFilters(BloodDonationFilter filter, Pageable pagingSort) {
        log.info("Fetching blood donations with filters: {}", filter);
        List<Filter> filterList = generateQueryFilters(filter);
        Specification<BloodDonation> specification = where(null);
        if (!filterList.isEmpty()) {
            specification = where(createSpecification(filterList.remove(0)));
            for (Filter input : filterList) {
                specification = specification.and(createSpecification(input));
            }
        }
        return bloodDonationRepository.findAll(specification, pagingSort);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BloodDonation> getAllBloodDonation() {
        return bloodDonationRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public BloodDonation getBloodDonationById(Long id) {
        validatePositiveId(id, "Blood donation");
        return bloodDonationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blood donation not found with id: " + id));
    }

    @Override
    public BloodDonation createBloodDonation(BloodDonation bloodDonation) {
        if (bloodDonation == null) {
            throw new BadRequestException("Blood donation payload is required");
        }
        hydrateReferences(bloodDonation);
        validateRequiredFields(bloodDonation);
        applyDefaults(bloodDonation);
        return bloodDonationRepository.save(bloodDonation);
    }

    @Override
    public BloodDonation updateBloodDonationById(BloodDonation bloodDonation, Long id) {
        if (bloodDonation == null) {
            throw new BadRequestException("Blood donation payload is required");
        }
        BloodDonation existingBloodDonation = getBloodDonationById(id);
        hydrateReferences(bloodDonation);
        validateRequiredFields(bloodDonation);
        applyDefaults(bloodDonation);
        existingBloodDonation.setDonor(bloodDonation.getDonor());
        existingBloodDonation.setHospital(bloodDonation.getHospital());
        existingBloodDonation.setDonationDate(bloodDonation.getDonationDate());
        existingBloodDonation.setBloodGroup(bloodDonation.getBloodGroup());
        existingBloodDonation.setBloodComponent(bloodDonation.getBloodComponent());
        existingBloodDonation.setQuantityMl(bloodDonation.getQuantityMl());
        return bloodDonationRepository.save(existingBloodDonation);
    }

    @Override
    public void deleteBloodDonationById(Long id) {
        BloodDonation existingBloodDonation = getBloodDonationById(id);
        bloodDonationRepository.delete(existingBloodDonation);

    }

    @Override
    @Transactional(readOnly = true)
    public List<BloodDonationResponse> getBloodDonationsByProfileId(Long id) {
        validatePositiveId(id, "Profile");
        log.info("Fetching blood donations for profile ID: {}", id);
        List<BloodDonation> bloodDonations = bloodDonationRepository.findAllByDonor_Id(id);
        return bloodDonations.stream().map(bloodDonationMapper::toResponse)
                .toList();
    }

    private List<Filter> generateQueryFilters(BloodDonationFilter filter) {
        List<Filter> filters = new ArrayList<>();

        if (filter.getDonorId() != null)
            filters.add(generateJoinTableFilter("id", "donor", JOIN, filter.getDonorId()));

        if (filter.getHospitalId() != null)
            filters.add(generateJoinTableFilter("id", "hospital", JOIN, filter.getHospitalId()));

        if (filter.getDonationDateFrom() != null)
            filters.add(generateIndividualFilter("donationDate", DATE_GREATER_THAN_EQUALS, filter.getDonationDateFrom()));

        if (filter.getDonationDateTo() != null)
            filters.add(generateIndividualFilter("donationDate", DATE_LESS_THAN_EQUALS, filter.getDonationDateTo()));

        if (filter.getBloodGroups() != null && !filter.getBloodGroups().isEmpty())
            filters.add(generateIndividualFilter("bloodGroup", IN, filter.getBloodGroups()));

        if (filter.getBloodComponents() != null && !filter.getBloodComponents().isEmpty())
            filters.add(generateIndividualFilter("bloodComponent", IN, filter.getBloodComponents()));

        if (filter.getMinQuantityMl() != null)
            filters.add(generateIndividualFilter("quantityMl", GREATER_THAN_EQUALS, filter.getMinQuantityMl()));

        if (filter.getMaxQuantityMl() != null)
            filters.add(generateIndividualFilter("quantityMl", LESS_THAN_EQUALS, filter.getMaxQuantityMl()));

        if (filter.getDistrictId() != null)
            filters.add(generateJoinTableFilter("id", "hospital.district", JOIN, filter.getDistrictId()));

        if (filter.getUpazilaId() != null)
            filters.add(generateJoinTableFilter("id", "hospital.upazila", JOIN, filter.getUpazilaId()));

        return filters;
    }

    private void hydrateReferences(BloodDonation bloodDonation) {
        Long donorId = bloodDonation.getDonor() != null ? bloodDonation.getDonor().getId() : null;
        Integer hospitalId = bloodDonation.getHospital() != null ? bloodDonation.getHospital().getId() : null;

        validatePositiveId(donorId, "Donor");
        validatePositiveId(hospitalId, "Hospital");

        Profile donor = profileRepository.findById(donorId)
                .orElseThrow(() -> new ResourceNotFoundException("Donor not found with id: " + donorId));
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with id: " + hospitalId));

        bloodDonation.setDonor(donor);
        bloodDonation.setHospital(hospital);
    }

    private void applyDefaults(BloodDonation bloodDonation) {
        if (bloodDonation.getBloodComponent() == null) {
            bloodDonation.setBloodComponent(BloodComponent.WHOLE_BLOOD);
        }
        if (bloodDonation.getQuantityMl() == null) {
            bloodDonation.setQuantityMl(450);
        }
    }

    private void validateRequiredFields(BloodDonation bloodDonation) {
        if (bloodDonation.getDonationDate() == null) {
            throw new BadRequestException("Donation date is required");
        }
        if (bloodDonation.getBloodGroup() == null) {
            throw new BadRequestException("Blood group is required");
        }
    }

    private void validatePositiveId(Long id, String name) {
        if (id == null || id <= 0) {
            throw new BadRequestException(name + " ID must be positive");
        }
    }

    private void validatePositiveId(Integer id, String name) {
        if (id == null || id <= 0) {
            throw new BadRequestException(name + " ID must be positive");
        }
    }
}
