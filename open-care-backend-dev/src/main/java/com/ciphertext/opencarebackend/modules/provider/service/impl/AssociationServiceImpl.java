package com.ciphertext.opencarebackend.modules.provider.service.impl;
import com.ciphertext.opencarebackend.modules.provider.dto.filter.AssociationFilter;
import com.ciphertext.opencarebackend.modules.provider.dto.request.AssociationRequest;
import com.ciphertext.opencarebackend.entity.Association;
import com.ciphertext.opencarebackend.enums.AssociationType;
import com.ciphertext.opencarebackend.enums.Country;
import com.ciphertext.opencarebackend.enums.Domain;
import com.ciphertext.opencarebackend.exception.BadRequestException;
import com.ciphertext.opencarebackend.exception.ResourceNotFoundException;
import com.ciphertext.opencarebackend.mapper.AssociationMapper;
import com.ciphertext.opencarebackend.modules.provider.repository.AssociationRepository;
import com.ciphertext.opencarebackend.modules.shared.repository.specification.Filter;
import com.ciphertext.opencarebackend.modules.provider.service.AssociationService;
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
public class AssociationServiceImpl implements AssociationService {

    private final AssociationRepository associationRepository;
    private final AssociationMapper associationMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<Association> getPaginatedDataWithFilters(AssociationFilter associationFilter, Pageable pageable) {
        log.info("Fetching associations with filters: {}", associationFilter);
        List<Filter> filterList = generateQueryFilters(associationFilter);
        Specification<Association> specification = where(null);
        if (!filterList.isEmpty()) {
            specification = where(createSpecification(filterList.removeFirst()));
            for (Filter input : filterList) {
                specification = specification.and(createSpecification(input));
            }
        }
        log.info("Fetching associations with filters: {}", associationFilter);
        return associationRepository.findAll(specification, pageable);
    }

    private List<Filter> generateQueryFilters(AssociationFilter associationFilter) {

        List<Filter> filters = new ArrayList<>();

        if (associationFilter.getName() != null)
            filters.add(generateIndividualFilter("name", LIKE, associationFilter.getName()));

        if (associationFilter.getBnName() != null)
            filters.add(generateIndividualFilter("bnName", LIKE, associationFilter.getBnName()));

        if (associationFilter.getMedicalSpecialityId() != null)
            filters.add(generateJoinTableFilter("id", "medicalSpeciality", JOIN, associationFilter.getMedicalSpecialityId()));

        if (associationFilter.getDistrictId() != null)
            filters.add(generateJoinTableFilter("id", "district", JOIN, associationFilter.getDistrictId()));

        if (associationFilter.getUpazilaId() != null)
            filters.add(generateJoinTableFilter("id", "upazila", JOIN, associationFilter.getUpazilaId()));

        if (associationFilter.getAssociationType() != null)
            filters.add(generateIndividualFilter("associationType", EQUALS, AssociationType.valueOf(associationFilter.getAssociationType())));

        if (associationFilter.getDomain() != null)
            filters.add(generateIndividualFilter("domain", EQUALS, Domain.valueOf(associationFilter.getDomain())));

        return filters;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Association> getAllAssociations() {
        return associationRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Association getAssociationById(Integer id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Association ID must be positive");
        }
        return associationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Association not found with id: " + id));
    }

    @Override
    public Association createAssociation(AssociationRequest request) {
        Association association = associationMapper.toEntity(request);
        applyDefaults(association);
        return associationRepository.save(association);
    }

    @Override
    public Association updateAssociationById(AssociationRequest request, Integer id) {
        Association existingAssociation = getAssociationById(id);
        associationMapper.partialUpdate(request, existingAssociation);
        applyDefaults(existingAssociation);
        return associationRepository.save(existingAssociation);
    }

    @Override
    public void deleteAssociationById(Integer id) {
        Association existingAssociation = getAssociationById(id);
        associationRepository.delete(existingAssociation);
    }

    private void applyDefaults(Association association) {
        if (association.getOriginCountry() == null) {
            association.setOriginCountry(Country.INDIA);
        }
        if (association.getIsActive() == null) {
            association.setIsActive(true);
        }
        if (association.getIsAffiliated() == null) {
            association.setIsAffiliated(false);
        }
    }
}
