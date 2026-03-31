package com.ciphertext.opencarebackend.modules.provider.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import com.ciphertext.opencarebackend.modules.user.dto.response.ProfileResponse;
@Getter
@Setter
public class NurseResponse {
    private Long id;
    private String bnmcNo;
    private LocalDate startDate;
    private Integer yearOfExperience;
    private String description;
    private Boolean isActive;
    private Boolean isVerified;
    private ProfileResponse profile;
}
