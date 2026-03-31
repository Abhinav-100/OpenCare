package com.ciphertext.opencarebackend.modules.user.service.impl;
import com.ciphertext.opencarebackend.modules.user.dto.filter.ProfileFilter;
import com.ciphertext.opencarebackend.entity.Profile;
import com.ciphertext.opencarebackend.enums.BloodGroup;
import com.ciphertext.opencarebackend.enums.Gender;
import com.ciphertext.opencarebackend.enums.UserType;
import com.ciphertext.opencarebackend.exception.BadRequestException;
import com.ciphertext.opencarebackend.exception.ResourceNotFoundException;
import com.ciphertext.opencarebackend.modules.user.repository.ProfileRepository;
import com.ciphertext.opencarebackend.modules.shared.repository.specification.Filter;
import com.ciphertext.opencarebackend.modules.user.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.ciphertext.opencarebackend.modules.shared.repository.specification.QueryFilterUtils.*;
import static com.ciphertext.opencarebackend.modules.shared.repository.specification.QueryOperator.*;
import static com.ciphertext.opencarebackend.modules.shared.repository.specification.SpecificationBuilder.createSpecification;
import static org.springframework.data.jpa.domain.Specification.where;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;

    @Override
    public Long getProfileCount() {
        return profileRepository.countAllByUserType(UserType.USER);
    }

    @Override
    public Profile getProfileByKeycloakUserId(String keycloakUserId) throws ResourceNotFoundException {
        log.info("Fetching profile for Keycloak user ID: {}", keycloakUserId);
        return profileRepository.findByKeycloakUserId(keycloakUserId)
                .orElseThrow(() -> {
                    log.error("Profile not found for Keycloak user ID: {}", keycloakUserId);
                    return new ResourceNotFoundException("Profile not found for Keycloak user ID: " + keycloakUserId);
                });
    }

    @Override
    public Profile getProfileById(Long id) throws ResourceNotFoundException {
        log.info("Fetching profile with ID: {}", id);
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Profile not found with ID: {}", id);
                    return new ResourceNotFoundException("Profile not found with ID: " + id);
                });
        authorizeProfileAccess(profile);
        return profile;
    }

    @Override
    public Profile updateProfileBykeycloakId(String keycloakUserId, Profile profile) {
        log.info("Updating profile for Keycloak user ID: {}", keycloakUserId);
        Profile existingProfile = getProfileByKeycloakUserId(keycloakUserId);
        existingProfile.setName(profile.getName());
        existingProfile.setEmail(profile.getEmail());
        existingProfile.setPhone(profile.getPhone());
        existingProfile.setDateOfBirth(profile.getDateOfBirth());
        existingProfile.setAddress(profile.getAddress());
        existingProfile.setDistrict(profile.getDistrict());
        existingProfile.setUpazila(profile.getUpazila());
        existingProfile.setUnion(profile.getUnion());
        existingProfile.setGender(profile.getGender());
        existingProfile.setDateOfBirth(profile.getDateOfBirth());
        return profileRepository.save(existingProfile);
    }

    @Override
    public Profile updateProfile(Long id, Profile profile) {
        log.info("Updating profile with ID: {}", id);
        Profile currentProfile = requireCurrentProfile();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!isAdmin(currentProfile, authentication)) {
            throw new AccessDeniedException("Only admins can update arbitrary profiles");
        }

        if (profile == null) {
            throw new BadRequestException("Profile payload is required");
        }

        Profile existingProfile = getProfileById(id);
        existingProfile.setName(profile.getName());
        existingProfile.setBnName(profile.getBnName());
        existingProfile.setImageUrl(profile.getImageUrl());
        existingProfile.setUsername(profile.getUsername());
        existingProfile.setKeycloakUserId(profile.getKeycloakUserId());
        existingProfile.setAddress(profile.getAddress());
        existingProfile.setUserType(profile.getUserType());
        existingProfile.setEmail(profile.getEmail());
        existingProfile.setPhone(profile.getPhone());
        existingProfile.setDateOfBirth(profile.getDateOfBirth());
        existingProfile.setAddress(profile.getAddress());
        existingProfile.setDistrict(profile.getDistrict());
        existingProfile.setUpazila(profile.getUpazila());
        existingProfile.setUnion(profile.getUnion());
        existingProfile.setGender(profile.getGender());
        return profileRepository.save(existingProfile);
    }

    @Override
    public Profile createProfile(Profile profile) {
        log.info("Creating new profile for Keycloak user ID: {}", profile.getKeycloakUserId());
        return profileRepository.save(profile);
    }

    @Override
    public Page<Profile> getPaginatedDataWithFilters(ProfileFilter profileFilter, Pageable pagingSort) {
        log.info("Fetching paginated profiles with filters: {}", profileFilter);
        List<Filter> filterList = generateQueryFilters(profileFilter);
        Specification<Profile> specification = where(null);
        if (!filterList.isEmpty()) {
            specification = where(createSpecification(filterList.removeFirst()));
            for (Filter input : filterList) {
                specification = specification.and(createSpecification(input));
            }
        }
        Page<Profile> profiles = profileRepository.findAll(specification, pagingSort);
        log.info("Retrieved {} profiles with applied filters", profiles.getTotalElements());
        return profiles;
    }

    private List<Filter> generateQueryFilters(ProfileFilter profileFilter) {
        List<Filter> filters = new ArrayList<>();

        if (profileFilter.getName() != null)
            filters.add(generateIndividualFilter("name", LIKE, profileFilter.getName()));

        if (profileFilter.getName() != null)
            filters.add(generateIndividualFilter("bnName", LIKE, profileFilter.getName()));

        if (profileFilter.getMinAge() != null)
            filters.add(generateIndividualFilter("dateOfBirth", DATE_GREATER_THAN_EQUALS,
                    calculateDateOfBirthFromAge(profileFilter.getMinAge())));

        if (profileFilter.getMaxAge() != null)
            filters.add(generateIndividualFilter("dateOfBirth", DATE_LESS_THAN_EQUALS,
                    calculateDateOfBirthFromAge(profileFilter.getMaxAge() + 1)));

        if (profileFilter.getEmail() != null)
            filters.add(generateIndividualFilter("email", LIKE, profileFilter.getEmail()));

        if (profileFilter.getPhone() != null)
            filters.add(generateIndividualFilter("phone", LIKE, profileFilter.getPhone()));

        if (profileFilter.getUserType() != null)
            filters.add(generateIndividualFilter("userType", EQUALS, UserType.valueOf(profileFilter.getUserType())));

        if (profileFilter.getGender() != null)
            filters.add(generateIndividualFilter("gender", EQUALS, Gender.valueOf(profileFilter.getGender())));

        if (profileFilter.getBloodGroup() != null)
            filters.add(generateIndividualFilter("bloodGroup", EQUALS, BloodGroup.valueOf(profileFilter.getBloodGroup())));

        if (profileFilter.getDistrictId() != null) {
            filters.add(generateJoinTableFilter("id", "district", JOIN, profileFilter.getDistrictId()));
        }

        if (profileFilter.getUpazilaId() != null) {
            filters.add(generateJoinTableFilter("id", "upazila", JOIN, profileFilter.getUpazilaId()));
        }

        if (profileFilter.getUnionId() != null) {
            filters.add(generateJoinTableFilter("id", "union", JOIN, profileFilter.getUnionId()));
        }

        return filters;
    }

    private LocalDateTime calculateDateOfBirthFromAge(Integer minAge) {
        LocalDateTime currentDate = LocalDateTime.now();
        return currentDate.minusYears(minAge);
    }

    private void authorizeProfileAccess(Profile targetProfile) {
        Profile currentProfile = requireCurrentProfile();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (isAdmin(currentProfile, authentication)) {
            return;
        }

        if (currentProfile.getId() != null && currentProfile.getId().equals(targetProfile.getId())) {
            return;
        }

        throw new AccessDeniedException("You are not allowed to access this profile");
    }

    private Profile requireCurrentProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Authentication is required");
        }

        String keycloakUserId = resolveCurrentUserId(authentication);
        if (!StringUtils.hasText(keycloakUserId)) {
            throw new AccessDeniedException("Unable to resolve authenticated user");
        }

        return profileRepository.findByKeycloakUserId(keycloakUserId)
                .orElseThrow(() -> new AccessDeniedException("Authenticated profile not found"));
    }

    private boolean isAdmin(Profile profile, Authentication authentication) {
        if (profile.getUserType() == UserType.ADMIN || profile.getUserType() == UserType.SUPER_ADMIN) {
            return true;
        }

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(StringUtils::hasText)
                .map(value -> value.toLowerCase(java.util.Locale.ROOT))
                .anyMatch(authority -> authority.equals("admin")
                        || authority.equals("super-admin")
                        || authority.equals("role_admin")
                        || authority.equals("role_super_admin")
                        || authority.equals("role_super-admin"));
    }

    private String resolveCurrentUserId(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof org.springframework.security.oauth2.jwt.Jwt jwt) {
            return jwt.getSubject();
        }

        String authenticationName = authentication.getName();
        if (StringUtils.hasText(authenticationName)) {
            return authenticationName;
        }

        return null;
    }
}
