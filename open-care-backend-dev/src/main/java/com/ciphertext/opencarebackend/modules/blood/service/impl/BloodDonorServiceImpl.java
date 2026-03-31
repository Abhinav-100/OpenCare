package com.ciphertext.opencarebackend.modules.blood.service.impl;
import com.ciphertext.opencarebackend.modules.blood.dto.filter.BloodDonorFilter;
import com.ciphertext.opencarebackend.entity.Profile;
import com.ciphertext.opencarebackend.modules.user.repository.ProfileRepository;
import com.ciphertext.opencarebackend.modules.shared.repository.specification.Filter;
import com.ciphertext.opencarebackend.modules.blood.service.BloodDonorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.ciphertext.opencarebackend.modules.shared.repository.specification.QueryFilterUtils.*;
import static com.ciphertext.opencarebackend.modules.shared.repository.specification.QueryOperator.*;
import static com.ciphertext.opencarebackend.modules.shared.repository.specification.SpecificationBuilder.createSpecification;
import static org.springframework.data.jpa.domain.Specification.where;

@Service
@RequiredArgsConstructor
@Slf4j
public class BloodDonorServiceImpl implements BloodDonorService {

    private final ProfileRepository profileRepository;

    @Override
    public Page<Profile> getPaginatedBloodDonorsWithFilters(BloodDonorFilter filter, Pageable pageable) {
        log.info("Fetching blood donors with filters: {}", filter);
        List<Filter> filterList = generateQueryFilters(filter);

        // Add mandatory filter for blood donors only
        filterList.add(generateIndividualFilter("isBloodDonor", EQUALS, true));

        Specification<Profile> specification = where(null);
        if (!filterList.isEmpty()) {
            specification = where(createSpecification(filterList.remove(0)));
            for (Filter input : filterList) {
                specification = specification.and(createSpecification(input));
            }
        }
        return profileRepository.findAll(specification, pageable);
    }

    private List<Filter> generateQueryFilters(BloodDonorFilter filter) {
        List<Filter> filters = new ArrayList<>();

        if (filter.getName() != null && !filter.getName().trim().isEmpty()) {
            filters.add(generateIndividualFilter("name", LIKE, filter.getName()));
        }

        if (filter.getPhone() != null && !filter.getPhone().trim().isEmpty()) {
            filters.add(generateIndividualFilter("phone", LIKE, filter.getPhone()));
        }

        if (filter.getGenders() != null && !filter.getGenders().isEmpty()) {
            filters.add(generateIndividualFilter("gender", IN, filter.getGenders()));
        }

        if (filter.getBloodGroups() != null && !filter.getBloodGroups().isEmpty()) {
            filters.add(generateIndividualFilter("bloodGroup", IN, filter.getBloodGroups()));
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

        if (filter.getMinDonationCount() != null) {
            filters.add(generateIndividualFilter("bloodDonationCount", GREATER_THAN_EQUALS, filter.getMinDonationCount()));
        }

        if (filter.getMaxDonationCount() != null) {
            filters.add(generateIndividualFilter("bloodDonationCount", LESS_THAN_EQUALS, filter.getMaxDonationCount()));
        }

        if (filter.getLastDonationDateFrom() != null) {
            filters.add(generateIndividualFilter("lastBloodDonationDate", DATE_GREATER_THAN_EQUALS, filter.getLastDonationDateFrom()));
        }

        if (filter.getLastDonationDateTo() != null) {
            filters.add(generateIndividualFilter("lastBloodDonationDate", DATE_LESS_THAN_EQUALS, filter.getLastDonationDateTo()));
        }

        if (filter.getIsActive() != null) {
            filters.add(generateIndividualFilter("isActive", EQUALS, filter.getIsActive()));
        }

        // Handle search text for multiple fields
        if (filter.getSearchText() != null && !filter.getSearchText().trim().isEmpty()) {
            // This will search in name, phone, and address fields
            filters.add(generateIndividualFilter("name", LIKE, filter.getSearchText()));
            // Note: For more complex OR conditions across multiple fields,
            // you might need to create a custom specification
        }

        return filters;
    }
}
