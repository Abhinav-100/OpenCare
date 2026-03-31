package com.ciphertext.opencarebackend.modules.provider.service.impl;
import com.ciphertext.opencarebackend.modules.provider.dto.filter.InstitutionFilter;
import com.ciphertext.opencarebackend.modules.provider.dto.request.InstitutionRequest;
import com.ciphertext.opencarebackend.entity.Institution;
import com.ciphertext.opencarebackend.entity.Tag;
import com.ciphertext.opencarebackend.enums.InstitutionType;
import com.ciphertext.opencarebackend.enums.OrganizationType;
import com.ciphertext.opencarebackend.exception.BadRequestException;
import com.ciphertext.opencarebackend.exception.ResourceNotFoundException;
import com.ciphertext.opencarebackend.exception.UnprocessableEntityException;
import com.ciphertext.opencarebackend.mapper.InstitutionMapper;
import com.ciphertext.opencarebackend.modules.provider.repository.DoctorDegreeRepository;
import com.ciphertext.opencarebackend.modules.provider.repository.InstitutionRepository;
import com.ciphertext.opencarebackend.modules.shared.repository.specification.Filter;
import com.ciphertext.opencarebackend.modules.shared.repository.specification.InJoin;
import com.ciphertext.opencarebackend.modules.provider.service.InstitutionService;
import com.ciphertext.opencarebackend.util.GisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.io.ParseException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.ciphertext.opencarebackend.modules.shared.repository.specification.QueryFilterUtils.*;
import static com.ciphertext.opencarebackend.modules.shared.repository.specification.QueryOperator.*;
import static com.ciphertext.opencarebackend.modules.shared.repository.specification.SpecificationBuilder.createSpecification;
import static org.springframework.data.jpa.domain.Specification.where;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class InstitutionServiceImpl implements InstitutionService {
    private final InstitutionRepository institutionRepository;
    private final DoctorDegreeRepository doctorDegreeRepository;
    private final InstitutionMapper institutionMapper;

    @Override
    public Long getInstitutionCount() {
        return institutionRepository.count();
    }

    @Override
    @Cacheable("institutions")
    public List<Institution> getAllInstitutions() {
        log.info("Fetching all institutions");
        List<Institution> institutions = institutionRepository.findAll();
        log.info("Retrieved {} institutions", institutions.size());
        return institutions;
    }

    @Override
    public Page<Institution> getPaginatedDataWithFilters(InstitutionFilter institutionFilter, Pageable pagingSort) {
        log.info("Fetching institutions with filters: {}", institutionFilter);
        List<Filter> filterList = generateQueryFilters(institutionFilter);
        Specification<Institution> specification = where(null);
        if(!filterList.isEmpty()) {
            specification = where(createSpecification(filterList.removeFirst()));
            for (Filter input : filterList) {
                specification = specification.and(createSpecification(input));
            }
        }
        log.info("Fetching institutions with filters: {}", institutionFilter);
        return institutionRepository.findAll(specification, pagingSort);
    }

    @Override
    @Caching(
            evict = @CacheEvict(value = "institutions", allEntries = true),
            put = @CachePut(value = "institutions", key = "#result.id")
    )
    public Institution createInstitution(Institution institution) {
        log.info("Creating new institution: {}", institution);
        try {
            Optional<Institution> existingInstitution = institutionRepository.findByName(institution.getName());

            if (existingInstitution.isPresent()) {
                String message = String.format(
                        "Institution with name '%s' and coordinates already exists with ID %d",
                        institution.getName(),
                        existingInstitution.get().getId()
                );
                throw new UnprocessableEntityException(message);
            }
            institution.setLocation(GisUtil.getLocation(institution.getLat(), institution.getLon()));
            Institution savedInstitution = institutionRepository.save(institution);
            log.info("Created institution with id: {}", savedInstitution.getId());
            return savedInstitution;
        }
        catch (ParseException e){
            log.error(e.getMessage());
        }
        return null;
    }

    // Java
    @Override
    @Caching(
            evict = @CacheEvict(value = "institutions", allEntries = true),
            put = @CachePut(value = "institutions", key = "#result.id")
    )
    public Institution updateInstitution(InstitutionRequest request, int id) {
        log.info("Updating institution with id: {}", id);

        Institution existingInstitution = institutionRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Institution not found with id: {}", id);
                    return new ResourceNotFoundException("Institution not found with id: " + id);
                });

        try {
            existingInstitution.setId(id); // ensure ID is not overridden
            institutionMapper.partialUpdate(request, existingInstitution);

            if (request.getLat() != null && request.getLon() != null) {
                existingInstitution.setLat(request.getLat());
                existingInstitution.setLon(request.getLon());
                existingInstitution.setLocation(GisUtil.getLocation(request.getLat(), request.getLon()));
            }

            Institution updatedInstitution = institutionRepository.save(existingInstitution);
            log.info("Updated institution with id: {}", updatedInstitution.getId());
            return updatedInstitution;
        } catch (org.locationtech.jts.io.ParseException e) {
            log.error("Error parsing location for institution id: {}", id, e);
            throw new RuntimeException("Invalid coordinates");
        } catch (Exception e) {
            log.error("Error updating institution with id: {}", id, e);
            throw new RuntimeException("Error updating institution: " + e.getMessage());
        }
    }

    @Override
    @Caching(
            evict = @CacheEvict(value = "institutions", allEntries = true),
            put = @CachePut(value = "institutions", key = "#result.id")
    )
    public Institution updateInstitutionWithTags(InstitutionRequest request, int id, Set<Tag> tags) {
        log.info("Updating institution with id: {} and {} tags", id, tags.size());

        Institution existingInstitution = institutionRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Institution not found with id: {}", id);
                    return new ResourceNotFoundException("Institution not found with id: " + id);
                });

        try {
            existingInstitution.setId(id); // ensure ID is not overridden
            institutionMapper.partialUpdate(request, existingInstitution);

            if (request.getLat() != null && request.getLon() != null) {
                existingInstitution.setLat(request.getLat());
                existingInstitution.setLon(request.getLon());
                existingInstitution.setLocation(GisUtil.getLocation(request.getLat(), request.getLon()));
            }

            // Update tags
            existingInstitution.getTags().clear();
            existingInstitution.getTags().addAll(tags);
            log.info("Updated institution tags: {}", tags.size());

            Institution updatedInstitution = institutionRepository.save(existingInstitution);
            log.info("Updated institution with id: {}", updatedInstitution.getId());
            return updatedInstitution;
        } catch (org.locationtech.jts.io.ParseException e) {
            log.error("Error parsing location for institution id: {}", id, e);
            throw new RuntimeException("Invalid coordinates");
        } catch (Exception e) {
            log.error("Error updating institution with id: {}", id, e);
            throw new RuntimeException("Error updating institution: " + e.getMessage());
        }
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "institutions", allEntries = true),
            @CacheEvict(value = "institutions", key = "#institutionId")
    })
    public ResponseEntity<Object> deleteInstitutionById(int institutionId) {
        log.info("Deleting institution with id: {}", institutionId);
        institutionRepository.findById(institutionId).orElseThrow(() -> {
            log.error("Institution not found with id: {}", institutionId);
            return new ResourceNotFoundException("Institution not found with id: " + institutionId);
        });
        checkInstitutionDependencies(institutionId);
        institutionRepository.deleteById(institutionId);
        if (institutionRepository.existsById(institutionId)) {
            return ResponseEntity.unprocessableEntity().body("Failed to delete the specified record");
        } else return ResponseEntity.ok().body("Institution is Deleted Successfully");
    }

    private void checkInstitutionDependencies(int institutionId) {
        List<String> usedInTables = new ArrayList<>();

        long doctorDegreeCount = doctorDegreeRepository.countAllByInstitutionId_Id(institutionId);

        if (doctorDegreeCount > 0) {
            usedInTables.add("Doctor Degree Count: " + doctorDegreeCount);
        }

        if (!usedInTables.isEmpty()) {
            String message = String.format(
                    "Institution with ID %d cannot be deleted because it is used in the following tables: %s",
                    institutionId, String.join(", ", usedInTables
                    )
            );
            throw new UnprocessableEntityException(message);
        }
    }

    @Override
    @Cacheable(value = "institutions", key = "#id")
    public Institution getInstitutionById(int id) throws ResourceNotFoundException {
        if (id <= 0) {
            log.error("Invalid institution ID: {}", id);
            throw new BadRequestException("Institution ID must be positive");
        }

        log.info("Fetching institution with id: {}", id);
        return institutionRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Institution not found with id: {}", id);
                    return new ResourceNotFoundException("Institution not found with id: " + id);
                });
    }

    public List<Filter> generateQueryFilters(InstitutionFilter institutionFilter) {

        List<Filter> filters = new ArrayList<>();

        if (institutionFilter.getName() != null)
            filters.add(generateIndividualFilter("name", LIKE, institutionFilter.getName()));

        if (institutionFilter.getBnName() != null)
            filters.add(generateIndividualFilter("bnName", LIKE, institutionFilter.getBnName()));

        if (institutionFilter.getEnroll() != null)
            filters.add(generateIndividualFilter("enroll", EQUALS, institutionFilter.getEnroll()));

        if (institutionFilter.getDistrictIds() != null && !institutionFilter.getDistrictIds().isEmpty()) {
            InJoin<Integer> inJoin = new InJoin<>("id", "district", "id",
                    institutionFilter.getDistrictIds());
            filters.add(generateJoinTableInFilter(inJoin, IN_JOIN));
        }

        if (institutionFilter.getInstitutionTypes() != null && !institutionFilter.getInstitutionTypes().isEmpty()) {
            List<InstitutionType> institutionTypes = new ArrayList<>();
            for (String institutionType : institutionFilter.getInstitutionTypes()) {
                institutionTypes.add(InstitutionType.valueOf(institutionType));
            }
            filters.add(generateInFilter("institutionType", IN, institutionTypes));
        }

        if (institutionFilter.getOrganizationType() != null)
            filters.add(generateIndividualFilter("organizationType", EQUALS, OrganizationType.valueOf(institutionFilter.getOrganizationType())));

        return filters;
    }
}
