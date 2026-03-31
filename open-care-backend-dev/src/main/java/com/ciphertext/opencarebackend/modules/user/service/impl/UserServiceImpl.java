package com.ciphertext.opencarebackend.modules.user.service.impl;
import com.ciphertext.opencarebackend.modules.user.dto.request.DomainUserRequest;
import com.ciphertext.opencarebackend.entity.Hospital;
import com.ciphertext.opencarebackend.entity.Institution;
import com.ciphertext.opencarebackend.entity.Profile;
import com.ciphertext.opencarebackend.entity.SocialOrganization;
import com.ciphertext.opencarebackend.entity.Association;
import com.ciphertext.opencarebackend.enums.UserType;
import com.ciphertext.opencarebackend.modules.provider.service.AssociationService;
import com.ciphertext.opencarebackend.modules.user.service.AssociationProfileService;
import com.ciphertext.opencarebackend.modules.provider.service.HospitalService;
import com.ciphertext.opencarebackend.modules.user.service.HospitalProfileService;
import com.ciphertext.opencarebackend.modules.provider.service.InstitutionService;
import com.ciphertext.opencarebackend.modules.user.service.InstitutionProfileService;
import com.ciphertext.opencarebackend.modules.user.service.ProfileService;
import com.ciphertext.opencarebackend.modules.provider.service.SocialOrganizationService;
import com.ciphertext.opencarebackend.modules.user.service.SocialOrganizationProfileService;
import com.ciphertext.opencarebackend.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final HospitalService hospitalService;
    private final ProfileService profileService;
    private final InstitutionService institutionService;
    private final SocialOrganizationService socialOrganizationService;
    private final AssociationService associationService;
    private final HospitalProfileService hospitalProfileService;
    private final InstitutionProfileService institutionProfileService;
    private final SocialOrganizationProfileService socialOrganizationProfileService;
    private final AssociationProfileService associationProfileService;

    @Override
    public void assignUserToDomains(Long profileId, DomainUserRequest domainUserRequest) {
        Profile profile = getProfileAndValidate(profileId);
        if (profile == null) return;

        UserType userType = UserType.valueOf(domainUserRequest.getUserType());
        Integer domainId = Integer.parseInt(domainUserRequest.getDomainId());

        switch (userType) {
            case HOSPITAL_USER, HOSPITAL_ADMIN -> assignToHospital(profile, domainId, domainUserRequest, userType);
            case INSTITUTION_USER, INSTITUTION_ADMIN -> assignToInstitution(profile, domainId, domainUserRequest, userType);
            case SOCIAL_ORGANIZATION_USER, SOCIAL_ORGANIZATION_ADMIN -> assignToSocialOrganization(profile, domainId, domainUserRequest, userType);
            case ASSOCIATION_USER, ASSOCIATION_ADMIN -> assignToAssociation(profile, domainId, domainUserRequest, userType);
            default -> log.warn("Unknown user type: {}", userType);
        }
    }

    @Override
    public void removeUserFromDomains(Long profileId, DomainUserRequest domainUserRequest) {
        Profile profile = getProfileAndValidate(profileId);
        if (profile == null) return;

        UserType userType = UserType.valueOf(domainUserRequest.getUserType());
        Integer domainId = Integer.parseInt(domainUserRequest.getDomainId());

        switch (userType) {
            case HOSPITAL_USER, HOSPITAL_ADMIN -> removeFromHospital(profileId, domainId);
            case INSTITUTION_USER, INSTITUTION_ADMIN -> removeFromInstitution(profileId, domainId);
            case SOCIAL_ORGANIZATION_USER, SOCIAL_ORGANIZATION_ADMIN -> removeFromSocialOrganization(profileId, domainId);
            case ASSOCIATION_USER, ASSOCIATION_ADMIN -> removeFromAssociation(profileId, domainId);
            default -> log.warn("Unknown user type for removal: {}", userType);
        }
    }

    private Profile getProfileAndValidate(Long profileId) {
        Profile profile = profileService.getProfileById(profileId);
        if (profile == null) {
            log.error("Profile with ID {} not found", profileId);
        }
        return profile;
    }

    private void assignToHospital(Profile profile, Integer hospitalId, DomainUserRequest request, UserType userType) {
        Hospital hospital = hospitalService.getHospitalById(hospitalId);
        if (hospital == null) {
            log.error("Hospital with ID {} not found", hospitalId);
            return;
        }

        if (hospitalProfileService.existsActiveHospitalProfile(profile.getId(), hospitalId)) {
            log.warn("Active hospital profile already exists for profile {} and hospital {}", profile.getId(), hospitalId);
            return;
        }

        profile.setUserType(userType);
        profileService.updateProfile(profile.getId(), profile);

        hospitalProfileService.createHospitalProfile(
            profile, hospital, request.getPosition(), request.getStartDate(), request.getEndDate()
        );

        log.info("Successfully assigned user {} to hospital domain with ID {}", profile.getId(), hospitalId);
    }

    private void assignToInstitution(Profile profile, Integer institutionId, DomainUserRequest request, UserType userType) {
        Institution institution = institutionService.getInstitutionById(institutionId);
        if (institution == null) {
            log.error("Institution with ID {} not found", institutionId);
            return;
        }

        if (institutionProfileService.existsActiveInstitutionProfile(profile.getId(), institutionId)) {
            log.warn("Active institution profile already exists for profile {} and institution {}", profile.getId(), institutionId);
            return;
        }

        profile.setUserType(userType);
        profileService.updateProfile(profile.getId(), profile);

        institutionProfileService.createInstitutionProfile(
            profile, institution, request.getPosition(), request.getStartDate(), request.getEndDate()
        );

        log.info("Successfully assigned user {} to institution domain with ID {}", profile.getId(), institutionId);
    }

    private void assignToSocialOrganization(Profile profile, Integer socialOrganizationId, DomainUserRequest request, UserType userType) {
        SocialOrganization socialOrganization = socialOrganizationService.getSocialOrganizationById(socialOrganizationId);
        if (socialOrganization == null) {
            log.error("Social Organization with ID {} not found", socialOrganizationId);
            return;
        }

        if (socialOrganizationProfileService.existsActiveSocialOrganizationProfile(profile.getId(), socialOrganizationId)) {
            log.warn("Active social organization profile already exists for profile {} and organization {}", profile.getId(), socialOrganizationId);
            return;
        }

        profile.setUserType(userType);
        profileService.updateProfile(profile.getId(), profile);

        socialOrganizationProfileService.createSocialOrganizationProfile(
            profile, socialOrganization, request.getPosition(), request.getStartDate(), request.getEndDate()
        );

        log.info("Successfully assigned user {} to social organization domain with ID {}", profile.getId(), socialOrganizationId);
    }

    private void assignToAssociation(Profile profile, Integer associationId, DomainUserRequest request, UserType userType) {
        Association association = associationService.getAssociationById(associationId);
        if (association == null) {
            log.error("Association with ID {} not found", associationId);
            return;
        }

        if (associationProfileService.existsActiveAssociationProfile(profile.getId(), associationId)) {
            log.warn("Active association profile already exists for profile {} and association {}", profile.getId(), associationId);
            return;
        }

        profile.setUserType(userType);
        profileService.updateProfile(profile.getId(), profile);

        associationProfileService.createAssociationProfile(
            profile, association, request.getPosition(), request.getStartDate(), request.getEndDate()
        );

        log.info("Successfully assigned user {} to association domain with ID {}", profile.getId(), associationId);
    }

    private void removeFromHospital(Long profileId, Integer hospitalId) {
        if (!hospitalProfileService.existsActiveHospitalProfile(profileId, hospitalId)) {
            log.warn("No active hospital profile found for profile {} and hospital {}", profileId, hospitalId);
            return;
        }

        Profile profile = profileService.getProfileById(profileId);
        profile.setUserType(UserType.USER);
        profileService.updateProfile(profileId, profile);

        hospitalProfileService.removeHospitalProfile(profileId, hospitalId);
        log.info("Successfully removed user {} from hospital domain with ID {}", profileId, hospitalId);
    }

    private void removeFromInstitution(Long profileId, Integer institutionId) {
        if (!institutionProfileService.existsActiveInstitutionProfile(profileId, institutionId)) {
            log.warn("No active institution profile found for profile {} and institution {}", profileId, institutionId);
            return;
        }

        Profile profile = profileService.getProfileById(profileId);
        profile.setUserType(UserType.USER);
        profileService.updateProfile(profileId, profile);

        institutionProfileService.removeInstitutionProfile(profileId, institutionId);
        log.info("Successfully removed user {} from institution domain with ID {}", profileId, institutionId);
    }

    private void removeFromSocialOrganization(Long profileId, Integer socialOrganizationId) {
        if (!socialOrganizationProfileService.existsActiveSocialOrganizationProfile(profileId, socialOrganizationId)) {
            log.warn("No active social organization profile found for profile {} and organization {}", profileId, socialOrganizationId);
            return;
        }

        Profile profile = profileService.getProfileById(profileId);
        profile.setUserType(UserType.USER);
        profileService.updateProfile(profileId, profile);

        socialOrganizationProfileService.removeSocialOrganizationProfile(profileId, socialOrganizationId);
        log.info("Successfully removed user {} from social organization domain with ID {}", profileId, socialOrganizationId);
    }

    private void removeFromAssociation(Long profileId, Integer associationId) {
        if (!associationProfileService.existsActiveAssociationProfile(profileId, associationId)) {
            log.warn("No active association profile found for profile {} and association {}", profileId, associationId);
            return;
        }

        Profile profile = profileService.getProfileById(profileId);
        profile.setUserType(UserType.USER);
        profileService.updateProfile(profileId, profile);

        associationProfileService.removeAssociationProfile(profileId, associationId);
        log.info("Successfully removed user {} from association domain with ID {}", profileId, associationId);
    }
}
