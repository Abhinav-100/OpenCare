package com.ciphertext.opencarebackend.modules.provider.service;
import com.ciphertext.opencarebackend.modules.provider.dto.filter.SocialOrganizationFilter;
import com.ciphertext.opencarebackend.modules.provider.dto.request.SocialOrganizationRequest;
import com.ciphertext.opencarebackend.entity.SocialOrganization;
import com.ciphertext.opencarebackend.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author Sadman
 */
public interface SocialOrganizationService {
    List<SocialOrganization> getAllSocialOrganizations();
    SocialOrganization getSocialOrganizationById(int id) throws ResourceNotFoundException;
    Page<SocialOrganization> getPaginatedDataWithFilters(SocialOrganizationFilter institutionFilter, Pageable pagingSort);
    SocialOrganization createSocialOrganization(SocialOrganizationRequest request);
    SocialOrganization updateSocialOrganization(int id, SocialOrganizationRequest request) throws ResourceNotFoundException;
    void deleteSocialOrganization(int id) throws ResourceNotFoundException;
    List<SocialOrganization> searchByTag(String tag);
}
