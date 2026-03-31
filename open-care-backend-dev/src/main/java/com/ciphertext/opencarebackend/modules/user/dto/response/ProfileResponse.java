package com.ciphertext.opencarebackend.modules.user.dto.response;
import com.ciphertext.opencarebackend.modules.blood.dto.response.enums.BloodGroupResponse;
import com.ciphertext.opencarebackend.modules.shared.dto.response.enums.GenderResponse;
import com.ciphertext.opencarebackend.modules.user.dto.response.enums.UserTypeResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import com.ciphertext.opencarebackend.modules.blood.dto.response.BloodDonationBadgeResponse;
import com.ciphertext.opencarebackend.modules.blood.dto.response.BloodDonationResponse;
import com.ciphertext.opencarebackend.modules.payment.dto.response.ContributionBadgeResponse;
import com.ciphertext.opencarebackend.modules.shared.dto.response.DistrictResponse;
import com.ciphertext.opencarebackend.modules.shared.dto.response.UnionResponse;
import com.ciphertext.opencarebackend.modules.shared.dto.response.UpazilaResponse;

@Getter
@Setter
public class ProfileResponse {
    private Long id;
    private String username;
    private UserTypeResponse userType;
    private String keycloakUserId;
    private String imageUrl;
    private String phone;
    private String email;
    private String name;
    private String bnName;
    private GenderResponse gender;
    private Date dateOfBirth;
    private BloodGroupResponse bloodGroup;
    private String address;
    private DistrictResponse district;
    private UpazilaResponse upazila;
    private UnionResponse union;
    private Integer contributionPoints;
    private Boolean isBloodDonor;
    private Integer bloodDonationCount;
    private Date lastBloodDonationDate;
    private Boolean isVolunteer;
    private Boolean healthDataConsent;
    private Boolean isActive;
    private String facebookProfileUrl;
    private String linkedinProfileUrl;
    private String xProfileUrl;
    private String researchGateProfileUrl;
    private String facebookPageUrl;
    private String instagramProfileUrl;
    private String youtubeChannelUrl;
    private String websiteUrl;
    private String blogUrl;
    private ContributionBadgeResponse contributionBadge;
    private BloodDonationBadgeResponse bloodDonationBadge;
    private UserActivityResponse userActivity;
    private List<BloodDonationResponse> bloodDonationList;
}