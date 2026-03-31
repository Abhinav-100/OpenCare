package com.ciphertext.opencarebackend.modules.user.controller;
import com.ciphertext.opencarebackend.modules.user.dto.filter.ProfileFilter;
import com.ciphertext.opencarebackend.modules.user.dto.request.ProfileRequest;
import com.ciphertext.opencarebackend.modules.user.dto.response.ProfileResponse;
import com.ciphertext.opencarebackend.entity.Profile;
import com.ciphertext.opencarebackend.entity.UserActivity;
import com.ciphertext.opencarebackend.mapper.ProfileMapper;
import com.ciphertext.opencarebackend.mapper.UserActivityMapper;
import com.ciphertext.opencarebackend.modules.blood.service.BloodDonationService;
import com.ciphertext.opencarebackend.modules.auth.service.KeycloakAdminService;
import com.ciphertext.opencarebackend.modules.user.service.ProfileService;
import com.ciphertext.opencarebackend.modules.user.service.UserActivityService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Slf4j
@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
@Tag(name = "Profile Management", description = "API for retrieving and updating user profiles")
public class ProfileApiController {
    private final UserActivityMapper userActivityMapper;

    private final ProfileService profileService;
    private final ProfileMapper profileMapper;
    private final KeycloakAdminService keycloakAdminService;
    private final BloodDonationService bloodDonationService;
    private final UserActivityService userActivityService;

    @Operation(
            summary = "Get paginated profiles with filters",
            description = """
                    Retrieves a paginated list of profiles with advanced filtering capabilities.
                    Supports filtering by:
                    - Name (English or Bengali)
                    - Phone number
                    - Email
                    - User type (Profile, Patient, Admin, etc.)
                    - Gender
                    - Age range
                    - Blood group
                    - Geographic location (district, upazila, union)
                    
                    Returns pagination metadata along with the results.
                    """,
            parameters = {
                    @Parameter(name = "name", description = "Filter by name (English or Bengali)"),
                    @Parameter(name = "phone", description = "Filter by phone number"),
                    @Parameter(name = "email", description = "Filter by email address"),
                    @Parameter(name = "userType", description = "Filter by user type (Profile, Patient, Admin, etc.)"),
                    @Parameter(name = "gender", description = "Filter by gender"),
                    @Parameter(name = "minAge", description = "Minimum age for age range filter"),
                    @Parameter(name = "maxAge", description = "Maximum age for age range filter"),
                    @Parameter(name = "bloodGroup", description = "Filter by blood group"),
                    @Parameter(name = "districtId", description = "Filter by district ID"),
                    @Parameter(name = "upazilaId", description = "Filter by upazila ID"),
                    @Parameter(name = "unionId", description = "Filter by union ID"),
                    @Parameter(name = "page", description = "Page number (default is 0)"),
                    @Parameter(name = "size", description = "Number of records per page (default is 5)"),
                    @Parameter(name = "sortBy", description = "Field to sort by (default is 'id')"),
                    @Parameter(name = "sortDir", description = "Sort direction: ASC or DESC (default is ASC)")
            }
    )
    @GetMapping
    public ResponseEntity<Map<String, Object>> getProfiles(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String userType,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(required = false) String bloodGroup,
            @RequestParam(required = false) Integer districtId,
            @RequestParam(required = false) Integer upazilaId,
            @RequestParam(required = false) Integer unionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pagingSort = PageRequest.of(page, size, Sort.by(direction, sortBy));

        ProfileFilter profileFilter = ProfileFilter.builder()
                .name(name)
                .phone(phone)
                .email(email)
                .userType(userType)
                .gender(gender)
                .minAge(minAge)
                .maxAge(maxAge)
                .bloodGroup(bloodGroup)
                .districtId(districtId)
                .upazilaId(upazilaId)
                .unionId(unionId)
                .build();

        Page<Profile> pageProfiles = profileService.getPaginatedDataWithFilters(profileFilter, pagingSort);

        Page<ProfileResponse> pageProfileResponses = pageProfiles.map(profileMapper::toResponse);

        Map<String, Object> response = new HashMap<>();
        response.put("profiles", pageProfileResponses.getContent());
        response.put("currentPage", pageProfiles.getNumber());
        response.put("totalItems", pageProfiles.getTotalElements());
        response.put("totalPages", pageProfiles.getTotalPages());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get profile by ID",
            description = """
                    Retrieves a profile by its unique ID along with associated information including:
                    - Blood donation history (if applicable)
                    - User activity logs (if applicable)
                    """,
            parameters = {
                    @Parameter(name = "id", description = "Profile ID", required = true),
                    @Parameter(name = "bloodDonations", description = "Include blood donation history", example = "true"),
                    @Parameter(name = "userActivity", description = "Include user activity logs", example = "true")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<ProfileResponse> getProfileById(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "false") boolean bloodDonations,
            @RequestParam(required = false, defaultValue = "false") boolean userActivity) {
        Profile profile = profileService.getProfileById(id);
        ProfileResponse profileResponse = profileMapper.toResponse(profile);

        if (bloodDonations) {
            profileResponse.setBloodDonationList(
                    bloodDonationService.getBloodDonationsByProfileId(id)
            );
        }

        if (userActivity) {
            UserActivity userActivity1 = userActivityService.getUserActivityByProfileId(id);
            if(userActivity1 != null) {
                profileResponse.setUserActivity(
                        userActivityMapper.toResponse(userActivity1)
                );
            }

        }
        return ResponseEntity.ok(profileResponse);
    }

    @Operation(summary = "Get current user's profile", description = "Retrieves the profile information for the authenticated user.")
    @GetMapping("/self")
    public ResponseEntity<ProfileResponse> getSelfProfile(Authentication authentication) {
        // Extract user info from the JWT token
        Jwt jwt = (Jwt) authentication.getPrincipal();

        Profile profile = profileService.getProfileByKeycloakUserId(jwt.getSubject());
        ProfileResponse profileResponse = profileMapper.toResponse(profile);

        UserRepresentation userRepresentation = keycloakAdminService.getUserById(jwt.getSubject());

        log.info("User representation: {}", userRepresentation);

        return ResponseEntity.ok(profileResponse);
    }

    @Operation(summary = "Update current user's profile", description = "Updates the profile details for the authenticated user.")
    @PutMapping("/self")
    public ResponseEntity<String> updateSelfProfile(
            @RequestBody ProfileRequest profileRequest,
            Authentication authentication) {
        // Extract user info from the JWT token
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String keycloakUserId = jwt.getSubject();
        log.info("Updating profile for Keycloak user ID: {}", keycloakUserId);
        UserRepresentation userRepresentation = keycloakAdminService.getUserById(keycloakUserId);
        userRepresentation.setUsername(profileRequest.getUsername());
        userRepresentation.setFirstName(profileRequest.getFirstName());
        userRepresentation.setLastName(profileRequest.getLastName());
        userRepresentation.setEmail(profileRequest.getEmail());

        Map<String, List<String>> attributes = userRepresentation.getAttributes();
        log.info("User attributes before update: {}", attributes);

        keycloakAdminService.updateUser(keycloakUserId, userRepresentation);

        // Update the profile in the database
        Profile profile = profileService.getProfileByKeycloakUserId(keycloakUserId);
        profileMapper.partialUpdate(profileRequest, profile);
        profileService.updateProfileBykeycloakId(keycloakUserId, profile);
        return ResponseEntity.ok("Profile updated successfully");
    }
}