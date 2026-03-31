package com.ciphertext.opencarebackend.modules.blood.service.impl;
import com.ciphertext.opencarebackend.modules.blood.dto.filter.BloodRequisitionFilter;
import com.ciphertext.opencarebackend.entity.BloodRequisition;
import com.ciphertext.opencarebackend.entity.District;
import com.ciphertext.opencarebackend.entity.Hospital;
import com.ciphertext.opencarebackend.entity.Profile;
import com.ciphertext.opencarebackend.entity.Upazila;
import com.ciphertext.opencarebackend.enums.RequisitionStatus;
import com.ciphertext.opencarebackend.exception.BadRequestException;
import com.ciphertext.opencarebackend.exception.ResourceNotFoundException;
import com.ciphertext.opencarebackend.modules.blood.repository.BloodRequisitionRepository;
import com.ciphertext.opencarebackend.modules.shared.repository.DistrictRepository;
import com.ciphertext.opencarebackend.modules.provider.repository.HospitalRepository;
import com.ciphertext.opencarebackend.modules.user.repository.ProfileRepository;
import com.ciphertext.opencarebackend.modules.shared.repository.UpazilaRepository;
import com.ciphertext.opencarebackend.modules.shared.repository.specification.Filter;
import com.ciphertext.opencarebackend.modules.blood.service.BloodRequisitionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
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
public class BloodRequisitionServiceImpl implements BloodRequisitionService {
    private final BloodRequisitionRepository bloodRequisitionRepository;
    private final ProfileRepository profileRepository;
    private final HospitalRepository hospitalRepository;
    private final DistrictRepository districtRepository;
    private final UpazilaRepository upazilaRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<BloodRequisition> getPaginatedDataWithFilters(BloodRequisitionFilter filter, Pageable pagingSort) {
        log.info("Fetching blood requisitions with filters: {}", filter);
        List<Filter> filterList = generateQueryFilters(filter);
        Specification<BloodRequisition> specification = where(null);
        if (!filterList.isEmpty()) {
            specification = where(createSpecification(filterList.remove(0)));
            for (Filter input : filterList) {
                specification = specification.and(createSpecification(input));
            }
        }
        return bloodRequisitionRepository.findAll(specification, pagingSort);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BloodRequisition> getAllBloodRequisition() {
        return bloodRequisitionRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public BloodRequisition getBloodRequisitionById(Long id) {
        validatePositiveId(id, "Blood requisition");
        return bloodRequisitionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blood Requisition not found with id: " + id));
    }

    @Override
    public BloodRequisition createBloodRequisition(BloodRequisition bloodRequisition) {
        if (bloodRequisition == null) {
            throw new BadRequestException("Blood requisition payload is required");
        }
        hydrateReferences(bloodRequisition);
        validateRequiredFields(bloodRequisition);
        applyDefaults(bloodRequisition);
        return bloodRequisitionRepository.save(bloodRequisition);
    }

    @Override
    public BloodRequisition updateBloodRequisitionById(BloodRequisition bloodRequisition, Long id) {
        if (bloodRequisition == null) {
            throw new BadRequestException("Blood requisition payload is required");
        }
        BloodRequisition existingBloodRequisition = getBloodRequisitionById(id);
        hydrateReferences(bloodRequisition);
        validateRequiredFields(bloodRequisition);

        existingBloodRequisition.setRequester(bloodRequisition.getRequester());
        existingBloodRequisition.setPatientName(bloodRequisition.getPatientName());
        existingBloodRequisition.setPatientAge(bloodRequisition.getPatientAge());
        existingBloodRequisition.setPatientGender(bloodRequisition.getPatientGender());
        existingBloodRequisition.setBloodGroup(bloodRequisition.getBloodGroup());
        existingBloodRequisition.setBloodComponent(bloodRequisition.getBloodComponent());
        existingBloodRequisition.setQuantityBags(bloodRequisition.getQuantityBags());
        existingBloodRequisition.setNeededByDate(bloodRequisition.getNeededByDate());
        existingBloodRequisition.setHospital(bloodRequisition.getHospital());
        existingBloodRequisition.setContactPerson(bloodRequisition.getContactPerson());
        existingBloodRequisition.setContactPhone(bloodRequisition.getContactPhone());
        existingBloodRequisition.setDescription(bloodRequisition.getDescription());
        existingBloodRequisition.setDistrict(bloodRequisition.getDistrict());
        existingBloodRequisition.setUpazila(bloodRequisition.getUpazila());
        existingBloodRequisition.setLat(bloodRequisition.getLat());
        existingBloodRequisition.setLon(bloodRequisition.getLon());
        if (bloodRequisition.getRequisitionStatus() != null) {
            existingBloodRequisition.setRequisitionStatus(bloodRequisition.getRequisitionStatus());
        }
        existingBloodRequisition.setFulfilledDate(bloodRequisition.getFulfilledDate());
        return bloodRequisitionRepository.save(existingBloodRequisition);
    }

    @Override
    public void deleteBloodRequisitionById(Long id) {
        BloodRequisition existingBloodRequisition = getBloodRequisitionById(id);
        bloodRequisitionRepository.delete(existingBloodRequisition);
    }

    private List<Filter> generateQueryFilters(BloodRequisitionFilter filter) {
        List<Filter> filters = new ArrayList<>();

        if (filter.getRequesterId() != null)
            filters.add(generateJoinTableFilter("id", "requester", JOIN, filter.getRequesterId()));

        if (filter.getPatientName() != null)
            filters.add(generateIndividualFilter("patientName", LIKE, filter.getPatientName()));

        if (filter.getMinPatientAge() != null)
            filters.add(generateIndividualFilter("patientAge", GREATER_THAN_EQUALS, filter.getMinPatientAge()));

        if (filter.getMaxPatientAge() != null)
            filters.add(generateIndividualFilter("patientAge", LESS_THAN_EQUALS, filter.getMaxPatientAge()));

        if (filter.getPatientGenders() != null && !filter.getPatientGenders().isEmpty())
            filters.add(generateIndividualFilter("patientGender", IN, filter.getPatientGenders()));

        if (filter.getBloodGroups() != null && !filter.getBloodGroups().isEmpty())
            filters.add(generateIndividualFilter("bloodGroup", IN, filter.getBloodGroups()));

        if (filter.getBloodComponents() != null && !filter.getBloodComponents().isEmpty())
            filters.add(generateIndividualFilter("bloodComponent", IN, filter.getBloodComponents()));

        if (filter.getMinQuantityBags() != null)
            filters.add(generateIndividualFilter("quantityBags", GREATER_THAN_EQUALS, filter.getMinQuantityBags()));

        if (filter.getMaxQuantityBags() != null)
            filters.add(generateIndividualFilter("quantityBags", LESS_THAN_EQUALS, filter.getMaxQuantityBags()));

        if (filter.getNeededByDateFrom() != null)
            filters.add(generateIndividualFilter("neededByDate", DATE_GREATER_THAN_EQUALS, filter.getNeededByDateFrom()));

        if (filter.getNeededByDateTo() != null)
            filters.add(generateIndividualFilter("neededByDate", DATE_LESS_THAN_EQUALS, filter.getNeededByDateTo()));

        if (filter.getHospitalId() != null)
            filters.add(generateJoinTableFilter("id", "hospital", JOIN, filter.getHospitalId()));

        if (filter.getContactPhone() != null)
            filters.add(generateIndividualFilter("contactPhone", LIKE, filter.getContactPhone()));

        if (filter.getDistrictId() != null)
            filters.add(generateJoinTableFilter("id", "district", JOIN, filter.getDistrictId()));

        if (filter.getUpazilaId() != null)
            filters.add(generateJoinTableFilter("id", "upazila", JOIN, filter.getUpazilaId()));

        if (filter.getStatuses() != null && !filter.getStatuses().isEmpty())
            filters.add(generateIndividualFilter("requisitionStatus", IN, filter.getStatuses()));

        if (filter.getFulfilledDateFrom() != null)
            filters.add(generateIndividualFilter("fulfilledDate", DATE_GREATER_THAN_EQUALS, filter.getFulfilledDateFrom()));

        if (filter.getFulfilledDateTo() != null)
            filters.add(generateIndividualFilter("fulfilledDate", LESS_THAN_EQUALS, filter.getFulfilledDateTo()));

        // Handle urgent requests (needed within 48 hours)
        if (filter.getIsUrgent() != null && filter.getIsUrgent()) {
            LocalDate urgentDate = LocalDate.now().plusDays(2);
            filters.add(generateIndividualFilter("neededByDate", DATE_LESS_THAN_EQUALS, urgentDate));
        }

        // Handle search text for multiple fields
        if (filter.getSearchText() != null && !filter.getSearchText().trim().isEmpty()) {
            // This will search in patient name, contact person, and description
            // Note: For more complex OR conditions, you might need to create a custom specification
            filters.add(generateIndividualFilter("patientName", LIKE, filter.getSearchText()));
            // You can add OR logic here if your specification builder supports it
        }

        return filters;
    }

    private void hydrateReferences(BloodRequisition bloodRequisition) {
        Long requesterId = bloodRequisition.getRequester() != null ? bloodRequisition.getRequester().getId() : null;
        Integer hospitalId = bloodRequisition.getHospital() != null ? bloodRequisition.getHospital().getId() : null;
        Integer districtId = bloodRequisition.getDistrict() != null ? bloodRequisition.getDistrict().getId() : null;
        Integer upazilaId = bloodRequisition.getUpazila() != null ? bloodRequisition.getUpazila().getId() : null;

        validatePositiveId(requesterId, "Requester");
        validatePositiveId(hospitalId, "Hospital");

        Profile requester = profileRepository.findById(requesterId)
                .orElseThrow(() -> new ResourceNotFoundException("Requester not found with id: " + requesterId));
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with id: " + hospitalId));

        bloodRequisition.setRequester(requester);
        bloodRequisition.setHospital(hospital);

        if (districtId != null) {
            District district = districtRepository.findById(districtId)
                    .orElseThrow(() -> new ResourceNotFoundException("District not found with id: " + districtId));
            bloodRequisition.setDistrict(district);
        }

        if (upazilaId != null) {
            Upazila upazila = upazilaRepository.findById(upazilaId)
                    .orElseThrow(() -> new ResourceNotFoundException("Upazila not found with id: " + upazilaId));
            bloodRequisition.setUpazila(upazila);
        }
    }

    private void applyDefaults(BloodRequisition bloodRequisition) {
        if (bloodRequisition.getRequisitionStatus() == null) {
            bloodRequisition.setRequisitionStatus(RequisitionStatus.ACTIVE);
        }
    }

    private void validateRequiredFields(BloodRequisition bloodRequisition) {
        if (!StringUtils.hasText(bloodRequisition.getPatientName())) {
            throw new BadRequestException("Patient name is required");
        }
        if (bloodRequisition.getBloodGroup() == null) {
            throw new BadRequestException("Blood group is required");
        }
        if (bloodRequisition.getQuantityBags() == null || bloodRequisition.getQuantityBags() <= 0) {
            throw new BadRequestException("Quantity must be positive");
        }
        if (bloodRequisition.getNeededByDate() == null) {
            throw new BadRequestException("Needed by date is required");
        }
        if (!StringUtils.hasText(bloodRequisition.getContactPhone())) {
            throw new BadRequestException("Contact phone is required");
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
