package com.ciphertext.opencarebackend.modules.user.service;
import com.ciphertext.opencarebackend.modules.user.dto.filter.ProfileFilter;
import com.ciphertext.opencarebackend.entity.Profile;
import com.ciphertext.opencarebackend.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProfileService {
    Long getProfileCount();
    Profile getProfileByKeycloakUserId(String keycloakUserId) throws ResourceNotFoundException;
    Profile getProfileById(Long id) throws ResourceNotFoundException;
    Profile updateProfileBykeycloakId(String keycloakUserId, Profile profile);
    Profile updateProfile(Long id, Profile profile);
    Profile createProfile(Profile profile);
    Page<Profile> getPaginatedDataWithFilters(ProfileFilter profileFilter, Pageable pagingSort);
}
