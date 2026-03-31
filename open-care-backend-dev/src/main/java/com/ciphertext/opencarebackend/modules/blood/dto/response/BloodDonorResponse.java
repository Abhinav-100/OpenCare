package com.ciphertext.opencarebackend.modules.blood.dto.response;
import com.ciphertext.opencarebackend.modules.blood.dto.response.enums.BloodGroupResponse;
import com.ciphertext.opencarebackend.modules.shared.dto.response.enums.GenderResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import com.ciphertext.opencarebackend.modules.shared.dto.response.DistrictResponse;
import com.ciphertext.opencarebackend.modules.shared.dto.response.UnionResponse;
import com.ciphertext.opencarebackend.modules.shared.dto.response.UpazilaResponse;

@Getter
@Setter
public class BloodDonorResponse {
    private Long id;
    private String name;
    private String bnName;
    private String phone;
    private String email;
    private GenderResponse gender;
    private BloodGroupResponse bloodGroup;
    private String address;
    private DistrictResponse district;
    private UpazilaResponse upazila;
    private UnionResponse union;
    private Integer bloodDonationCount;
    private Date lastBloodDonationDate;
    private String imageUrl;
    private Boolean isActive;
}