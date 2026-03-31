package com.ciphertext.opencarebackend.mapper;
import com.ciphertext.opencarebackend.modules.user.dto.request.ProfileRequest;
import com.ciphertext.opencarebackend.modules.blood.dto.response.BloodDonationBadgeResponse;
import com.ciphertext.opencarebackend.modules.payment.dto.response.ContributionBadgeResponse;
import com.ciphertext.opencarebackend.modules.user.dto.response.ProfileResponse;
import com.ciphertext.opencarebackend.modules.blood.dto.response.enums.BloodGroupResponse;
import com.ciphertext.opencarebackend.modules.shared.dto.response.enums.GenderResponse;
import com.ciphertext.opencarebackend.modules.user.dto.response.enums.UserTypeResponse;
import com.ciphertext.opencarebackend.entity.Profile;
import com.ciphertext.opencarebackend.enums.BloodGroup;
import com.ciphertext.opencarebackend.enums.Gender;
import com.ciphertext.opencarebackend.enums.UserType;
import com.ciphertext.opencarebackend.util.MinioUtil;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class ProfileMapper {

    private final MinioUtil minioUtil;
    private final DistrictMapper districtMapper;
    private final UpazilaMapper upazilaMapper;
    private final UnionMapper unionMapper;

    public ProfileResponse toResponse(Profile profile) {
        if (profile == null) {
            return null;
        }

        ProfileResponse response = new ProfileResponse();
        response.setId(profile.getId());
        response.setUsername(profile.getUsername());
        response.setUserType(userTypeEnumToResponse(profile.getUserType()));
        response.setKeycloakUserId(profile.getKeycloakUserId());
        response.setImageUrl(profile.getImageUrl());
        response.setPhone(profile.getPhone());
        response.setEmail(profile.getEmail());
        response.setName(profile.getName());
        response.setBnName(profile.getBnName());
        response.setGender(genderEnumToResponse(profile.getGender()));
        response.setDateOfBirth(profile.getDateOfBirth());
        response.setBloodGroup(bloodGroupEnumToResponse(profile.getBloodGroup()));
        response.setAddress(profile.getAddress());
        response.setDistrict(districtMapper.toResponse(profile.getDistrict()));
        response.setUpazila(upazilaMapper.toResponse(profile.getUpazila()));
        response.setUnion(unionMapper.toResponse(profile.getUnion()));
        response.setContributionPoints(profile.getContributionPoints());
        response.setIsBloodDonor(profile.getIsBloodDonor());
        response.setBloodDonationCount(profile.getBloodDonationCount());
        response.setLastBloodDonationDate(profile.getLastBloodDonationDate());
        response.setIsVolunteer(profile.getIsVolunteer());
        response.setHealthDataConsent(profile.getHealthDataConsent());
        response.setIsActive(profile.getIsActive());
        response.setFacebookProfileUrl(profile.getFacebookProfileUrl());
        response.setLinkedinProfileUrl(profile.getLinkedinProfileUrl());
        response.setXProfileUrl(profile.getXProfileUrl());
        response.setResearchGateProfileUrl(profile.getResearchGateProfileUrl());
        response.setFacebookPageUrl(profile.getFacebookPageUrl());
        response.setInstagramProfileUrl(profile.getInstagramProfileUrl());
        response.setYoutubeChannelUrl(profile.getYoutubeChannelUrl());
        response.setWebsiteUrl(profile.getWebsiteUrl());
        response.setBlogUrl(profile.getBlogUrl());

        generatePresignedUrls(response);
        return response;
    }

    protected void generatePresignedUrls(ProfileResponse response) {
        if (response.getImageUrl() != null) {
            response.setImageUrl(minioUtil.getPresignedUrl(response.getImageUrl()));
        }

        // Generate contribution badge from contribution points
        response.setContributionBadge(
            ContributionBadgeResponse.fromContributionPoints(response.getContributionPoints())
        );

        // Generate blood donation badge from blood donation count and last donation date
        response.setBloodDonationBadge(
            BloodDonationBadgeResponse.fromBloodDonationData(
                response.getBloodDonationCount(),
                response.getLastBloodDonationDate()
            )
        );
    }

    public Profile toEntity(ProfileRequest request) {
        if (request == null) {
            return null;
        }

        Profile profile = new Profile();
        profile.setUsername(request.getUsername());
        profile.setUserType(userTypeStringToEnum(request.getUserType()));
        profile.setImageUrl(request.getImageUrl());
        profile.setPhone(request.getPhone());
        profile.setEmail(request.getEmail());
        profile.setName(request.getName());
        profile.setBnName(request.getBnName());
        profile.setGender(genderStringToEnum(request.getGender()));
        profile.setDateOfBirth(toDate(request.getDateOfBirth()));
        profile.setBloodGroup(bloodGroupStringToEnum(request.getBloodGroup()));
        profile.setAddress(request.getAddress());
        profile.setIsBloodDonor(request.getIsBloodDonor());
        profile.setBloodDonationCount(request.getBloodDonationCount());
        profile.setLastBloodDonationDate(toDate(request.getLastBloodDonationDate()));
        profile.setIsVolunteer(request.getIsVolunteer());
        profile.setHealthDataConsent(request.getHealthDataConsent());
        profile.setIsActive(request.getIsActive());
        profile.setFacebookProfileUrl(request.getFacebookProfileUrl());
        profile.setLinkedinProfileUrl(request.getLinkedinProfileUrl());
        profile.setXProfileUrl(request.getXProfileUrl());
        profile.setResearchGateProfileUrl(request.getResearchGateProfileUrl());
        return profile;
    }

    public void partialUpdate(ProfileRequest request, Profile profile) {
        if (request == null || profile == null) {
            return;
        }

        if (request.getUsername() != null) {
            profile.setUsername(request.getUsername());
        }
        if (request.getUserType() != null) {
            profile.setUserType(userTypeStringToEnum(request.getUserType()));
        }
        if (request.getImageUrl() != null) {
            profile.setImageUrl(request.getImageUrl());
        }
        if (request.getPhone() != null) {
            profile.setPhone(request.getPhone());
        }
        if (request.getEmail() != null) {
            profile.setEmail(request.getEmail());
        }
        if (request.getName() != null) {
            profile.setName(request.getName());
        }
        if (request.getBnName() != null) {
            profile.setBnName(request.getBnName());
        }
        if (request.getGender() != null) {
            profile.setGender(genderStringToEnum(request.getGender()));
        }
        if (request.getDateOfBirth() != null) {
            profile.setDateOfBirth(toDate(request.getDateOfBirth()));
        }
        if (request.getBloodGroup() != null) {
            profile.setBloodGroup(bloodGroupStringToEnum(request.getBloodGroup()));
        }
        if (request.getAddress() != null) {
            profile.setAddress(request.getAddress());
        }
        if (request.getIsBloodDonor() != null) {
            profile.setIsBloodDonor(request.getIsBloodDonor());
        }
        if (request.getBloodDonationCount() != null) {
            profile.setBloodDonationCount(request.getBloodDonationCount());
        }
        if (request.getLastBloodDonationDate() != null) {
            profile.setLastBloodDonationDate(toDate(request.getLastBloodDonationDate()));
        }
        if (request.getIsVolunteer() != null) {
            profile.setIsVolunteer(request.getIsVolunteer());
        }
        if (request.getHealthDataConsent() != null) {
            profile.setHealthDataConsent(request.getHealthDataConsent());
        }
        if (request.getIsActive() != null) {
            profile.setIsActive(request.getIsActive());
        }
        if (request.getFacebookProfileUrl() != null) {
            profile.setFacebookProfileUrl(request.getFacebookProfileUrl());
        }
        if (request.getLinkedinProfileUrl() != null) {
            profile.setLinkedinProfileUrl(request.getLinkedinProfileUrl());
        }
        if (request.getXProfileUrl() != null) {
            profile.setXProfileUrl(request.getXProfileUrl());
        }
        if (request.getResearchGateProfileUrl() != null) {
            profile.setResearchGateProfileUrl(request.getResearchGateProfileUrl());
        }
    }

    protected GenderResponse genderEnumToResponse(Gender gender) {
        return gender != null ? gender.toResponse() : null;
    }

    protected Gender genderStringToEnum(String gender) {
        return gender != null ? Gender.valueOf(gender) : null;
    }

    protected UserTypeResponse userTypeEnumToResponse(UserType userType) {
        return userType != null ? userType.toResponse() : null;
    }

    protected UserType userTypeStringToEnum(String userType) {
        return userType != null ? UserType.valueOf(userType) : null;
    }

    protected BloodGroupResponse bloodGroupEnumToResponse(BloodGroup bloodGroup) {
        return bloodGroup != null ? bloodGroup.toResponse() : null;
    }

    protected BloodGroup bloodGroupStringToEnum(String bloodGroup) {
        return bloodGroup != null ? BloodGroup.valueOf(bloodGroup) : null;
    }

    private Date toDate(LocalDate value) {
        if (value == null) {
            return null;
        }
        return Date.from(value.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private Date toDate(LocalDateTime value) {
        if (value == null) {
            return null;
        }
        return Date.from(value.atZone(ZoneId.systemDefault()).toInstant());
    }
}
