package com.ciphertext.opencarebackend.modules.provider.service;
import com.ciphertext.opencarebackend.modules.provider.dto.filter.AssociationFilter;
import com.ciphertext.opencarebackend.modules.provider.dto.request.AssociationRequest;
import com.ciphertext.opencarebackend.entity.Association;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AssociationService {

    Page<Association> getPaginatedDataWithFilters(AssociationFilter associationFilter, Pageable pageable);

    List<Association> getAllAssociations();

    Association getAssociationById(Integer id);

    Association createAssociation(AssociationRequest request);

    Association updateAssociationById(AssociationRequest request, Integer id);

    void deleteAssociationById(Integer id);
}
