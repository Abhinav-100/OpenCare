package com.ciphertext.opencarebackend.modules.catalog.service;
import com.ciphertext.opencarebackend.modules.catalog.dto.filter.TagFilter;
import com.ciphertext.opencarebackend.modules.catalog.dto.request.TagRequest;
import com.ciphertext.opencarebackend.entity.Tag;
import com.ciphertext.opencarebackend.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TagService {

    List<Tag> getAllTags();

    Page<Tag> getAllTags(Pageable pageable);

    Page<Tag> getFilteredTags(TagFilter filter, Pageable pageable);

    Optional<Tag> getTagById(Integer id);

    Optional<Tag> getTagByName(String name);

    List<Tag> getTagsByCategory(String category);

    List<Tag> searchTags(String query);

    List<Tag> searchTagsByCategory(String category, String query);

    Set<Tag> getTagsByIds(Set<Long> ids);

    Set<Tag> getTagsByNames(Set<String> names);

    Tag createTag(TagRequest tagRequest);

    Tag updateTag(TagRequest tagRequest, Integer id) throws ResourceNotFoundException;

    void deleteTagById(Integer id) throws ResourceNotFoundException;

    List<Tag> fuzzySearchTags(String query, int limit);

    boolean existsById(Integer id);

    boolean existsByName(String name);
}
