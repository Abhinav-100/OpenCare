package com.ciphertext.opencarebackend.modules.provider.service;
import com.ciphertext.opencarebackend.modules.provider.dto.filter.InstitutionFilter;
import com.ciphertext.opencarebackend.modules.provider.dto.request.InstitutionRequest;
import com.ciphertext.opencarebackend.entity.Institution;
import com.ciphertext.opencarebackend.entity.Tag;
import com.ciphertext.opencarebackend.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Set;

/**
 * @author Sadman
 */
public interface InstitutionService {
    Long getInstitutionCount();
    List<Institution> getAllInstitutions();
    Institution getInstitutionById(int id) throws ResourceNotFoundException;
    Page<Institution> getPaginatedDataWithFilters(InstitutionFilter institutionFilter, Pageable pagingSort);
    Institution createInstitution(Institution institution);
    Institution updateInstitution(InstitutionRequest institution, int id);
    Institution updateInstitutionWithTags(InstitutionRequest institution, int id, Set<Tag> tags);
    ResponseEntity<Object> deleteInstitutionById(int id);
}
