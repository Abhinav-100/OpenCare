package com.ciphertext.opencarebackend.modules.provider.service.impl;
import com.ciphertext.opencarebackend.modules.provider.dto.filter.SocialOrganizationFilter;
import com.ciphertext.opencarebackend.modules.provider.dto.request.SocialOrganizationRequest;
import com.ciphertext.opencarebackend.entity.SocialOrganization;
import com.ciphertext.opencarebackend.entity.Tag;
import com.ciphertext.opencarebackend.exception.ResourceNotFoundException;
import com.ciphertext.opencarebackend.mapper.SocialOrganizationMapper;
import com.ciphertext.opencarebackend.modules.provider.repository.SocialOrganizationRepository;
import com.ciphertext.opencarebackend.modules.catalog.repository.TagRepository;
import com.ciphertext.opencarebackend.modules.provider.service.SocialOrganizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SocialOrganizationServiceImpl implements SocialOrganizationService {

    private final SocialOrganizationRepository socialOrganizationRepository;
    private final SocialOrganizationMapper socialOrganizationMapper;
    private final TagRepository tagRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SocialOrganization> getAllSocialOrganizations() {
        log.info("Retrieving all social organizations");
        return socialOrganizationRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public SocialOrganization getSocialOrganizationById(int id) throws ResourceNotFoundException {
        log.info("Retrieving social organization with ID: {}", id);
        return socialOrganizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Social Organization not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SocialOrganization> getPaginatedDataWithFilters(SocialOrganizationFilter filter, Pageable pagingSort) {
        log.info("Retrieving paginated social organizations with filters: {}", filter);

        Specification<SocialOrganization> spec = Specification.where(null);

        if (filter.getName() != null && !filter.getName().trim().isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
                            "%" + filter.getName().toLowerCase() + "%"));
        }

        if (filter.getBnName() != null && !filter.getBnName().trim().isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("bnName")),
                            "%" + filter.getBnName().toLowerCase() + "%"));
        }

        if (filter.getPhone() != null && !filter.getPhone().trim().isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get("phone"), "%" + filter.getPhone() + "%"));
        }

        if (filter.getEmail() != null && !filter.getEmail().trim().isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("email")),
                            "%" + filter.getEmail().toLowerCase() + "%"));
        }

        if (filter.getSocialOrganizationType() != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("socialOrganizationType"), filter.getSocialOrganizationType()));
        }

        if (filter.getAddress() != null && !filter.getAddress().trim().isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("address")),
                            "%" + filter.getAddress().toLowerCase() + "%"));
        }

        if (filter.getOriginCountry() != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("originCountry"), filter.getOriginCountry()));
        }

        return socialOrganizationRepository.findAll(spec, pagingSort);
    }

    @Override
    public SocialOrganization createSocialOrganization(SocialOrganizationRequest request) {
        log.info("Creating new social organization: {}", request.getName());

        SocialOrganization socialOrganization = socialOrganizationMapper.toEntity(request);
        // Auditable fields are handled automatically

        // Replace detached Tag instances (created by mapper) with managed Tag entities from DB
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            List<Tag> managedTags = tagRepository.findAllById(request.getTagIds());
            Set<Tag> tagSet = new HashSet<>(managedTags);
            socialOrganization.setTags(tagSet);
        } else {
            socialOrganization.setTags(new HashSet<>());
        }

        SocialOrganization savedSocialOrganization = socialOrganizationRepository.save(socialOrganization);
        log.info("Successfully created social organization with ID: {}", savedSocialOrganization.getId());

        return savedSocialOrganization;
    }

    @Override
    public SocialOrganization updateSocialOrganization(int id, SocialOrganizationRequest request) throws ResourceNotFoundException {
        log.info("Updating social organization with ID: {}", id);

        SocialOrganization existingSocialOrganization = getSocialOrganizationById(id);

        // Update fields
        socialOrganizationMapper.partialUpdate(request, existingSocialOrganization);
        // Auditable fields are handled automatically

        SocialOrganization updatedSocialOrganization = socialOrganizationRepository.save(existingSocialOrganization);
        log.info("Successfully updated social organization with ID: {}", id);

        return updatedSocialOrganization;
    }

    @Override
    public void deleteSocialOrganization(int id) throws ResourceNotFoundException {
        log.info("Deleting social organization with ID: {}", id);

        SocialOrganization socialOrganization = getSocialOrganizationById(id);
        socialOrganizationRepository.delete(socialOrganization);

        log.info("Successfully deleted social organization with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SocialOrganization> searchByTag(String tag) {
        log.info("Searching social organizations by tag: {}", tag);
        return socialOrganizationRepository.findByTagsContainingIgnoreCase(tag);
    }
}