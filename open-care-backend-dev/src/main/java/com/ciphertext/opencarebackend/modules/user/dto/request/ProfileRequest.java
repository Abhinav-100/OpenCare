package com.ciphertext.opencarebackend.modules.user.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class ProfileRequest {
    private String username;
    private String userType;
    private byte[] photo;
    private String imageUrl;
    private String phone;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String name;
    private String bnName;
    private String gender;
    private LocalDate dateOfBirth;
    private String bloodGroup;
    private String address;
    private Integer districtId;
    private Integer upazilaId;
    private Integer unionId;
    private Boolean isBloodDonor;
    private Integer bloodDonationCount;
    private LocalDateTime lastBloodDonationDate;
    private Boolean isVolunteer;
    private Boolean healthDataConsent;
    private Boolean isActive;
    private String facebookProfileUrl;
    private String linkedinProfileUrl;
    private String xProfileUrl;
    private String researchGateProfileUrl;
}
