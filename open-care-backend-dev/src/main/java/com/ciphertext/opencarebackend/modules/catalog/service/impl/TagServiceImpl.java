package com.ciphertext.opencarebackend.modules.catalog.service.impl;
import com.ciphertext.opencarebackend.modules.catalog.dto.filter.TagFilter;
import com.ciphertext.opencarebackend.modules.catalog.dto.request.TagRequest;
import com.ciphertext.opencarebackend.entity.Tag;
import com.ciphertext.opencarebackend.exception.ResourceNotFoundException;
import com.ciphertext.opencarebackend.mapper.TagMapper;
import com.ciphertext.opencarebackend.modules.catalog.repository.TagRepository;
import com.ciphertext.opencarebackend.modules.catalog.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    @Override
    @Transactional(readOnly = true)
    public List<Tag> getAllTags() {
        log.debug("Getting all tags");
        return tagRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Tag> getAllTags(Pageable pageable) {
        log.debug("Getting all tags with pagination: {}", pageable);
        return tagRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Tag> getFilteredTags(TagFilter filter, Pageable pageable) {
        log.debug("Getting filtered tags with filter: {} and pagination: {}", filter, pageable);

        Specification<Tag> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getName() != null && !filter.getName().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")),
                    "%" + filter.getName().toLowerCase() + "%"
                ));
            }

            if (filter.getCategory() != null && !filter.getCategory().trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("category"), filter.getCategory()));
            }

            if (filter.getQuery() != null && !filter.getQuery().trim().isEmpty()) {
                String queryLower = "%" + filter.getQuery().toLowerCase() + "%";
                Predicate namePredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")), queryLower
                );
                Predicate displayNamePredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("displayName")), queryLower
                );
                predicates.add(criteriaBuilder.or(namePredicate, displayNamePredicate));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return tagRepository.findAll(spec, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Tag> getTagById(Integer id) {
        log.debug("Getting tag by ID: {}", id);
        return tagRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Tag> getTagByName(String name) {
        log.debug("Getting tag by name: {}", name);
        return tagRepository.findByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tag> getTagsByCategory(String category) {
        log.debug("Getting tags by category: {}", category);
        return tagRepository.findByCategory(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tag> searchTags(String query) {
        log.debug("Searching tags with query: {}", query);
        return tagRepository.searchByNameOrDisplayName(query);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tag> searchTagsByCategory(String category, String query) {
        log.debug("Searching tags by category: {} and query: {}", category, query);
        return tagRepository.searchByCategoryAndQuery(category, query);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Tag> getTagsByIds(Set<Long> ids) {
        log.debug("Getting tags by IDs: {}", ids);
        return tagRepository.findByIdIn(ids);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Tag> getTagsByNames(Set<String> names) {
        log.debug("Getting tags by names: {}", names);
        return tagRepository.findByNameIn(names);
    }

    @Override
    public Tag createTag(TagRequest tagRequest) {
        log.debug("Creating new tag: {}", tagRequest);

        // Check if tag with same name already exists
        if (tagRepository.findByName(tagRequest.getName()).isPresent()) {
            throw new IllegalArgumentException("Tag with name '" + tagRequest.getName() + "' already exists");
        }

        Tag tag = tagMapper.toEntity(tagRequest);
        Tag savedTag = tagRepository.save(tag);
        log.info("Created tag with ID: {}", savedTag.getId());
        return savedTag;
    }

    @Override
    public Tag updateTag(TagRequest tagRequest, Integer id) throws ResourceNotFoundException {
        log.debug("Updating tag with ID: {} using request: {}", id, tagRequest);

        Tag existingTag = tagRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Tag not found with ID: " + id));

        // Check if another tag with the same name exists (excluding current tag)
        Optional<Tag> tagWithSameName = tagRepository.findByName(tagRequest.getName());
        if (tagWithSameName.isPresent() && !tagWithSameName.get().getId().equals(id)) {
            throw new IllegalArgumentException("Tag with name '" + tagRequest.getName() + "' already exists");
        }

        tagMapper.updateEntityFromRequest(tagRequest, existingTag);
        Tag updatedTag = tagRepository.save(existingTag);
        log.info("Updated tag with ID: {}", updatedTag.getId());
        return updatedTag;
    }

    @Override
    public void deleteTagById(Integer id) throws ResourceNotFoundException {
        log.debug("Deleting tag with ID: {}", id);

        if (!tagRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tag not found with ID: " + id);
        }

        tagRepository.deleteById(id);
        log.info("Deleted tag with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tag> fuzzySearchTags(String query, int limit) {
        log.debug("Performing fuzzy search for tags with query: {} and limit: {}", query, limit);
        return tagRepository.fuzzySearch(query, limit);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Integer id) {
        return tagRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return tagRepository.findByName(name).isPresent();
    }
}
