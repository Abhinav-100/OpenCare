package com.ciphertext.opencarebackend.modules.blood.dto.response;
import com.ciphertext.opencarebackend.modules.blood.dto.response.enums.BloodComponentResponse;
import com.ciphertext.opencarebackend.modules.blood.dto.response.enums.BloodGroupResponse;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import com.ciphertext.opencarebackend.modules.provider.dto.response.HospitalResponse;
import com.ciphertext.opencarebackend.modules.user.dto.response.ProfileResponse;

@Getter
@Setter
public class BloodDonationResponse {
    private Long id;
    private ProfileResponse donor;
    private HospitalResponse hospital;
    private LocalDate donationDate;
    private BloodGroupResponse bloodGroup;
    private Integer quantityMl;
    private BloodComponentResponse bloodComponent;
}