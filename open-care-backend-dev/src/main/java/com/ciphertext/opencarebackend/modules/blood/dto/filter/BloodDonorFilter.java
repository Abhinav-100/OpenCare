package com.ciphertext.opencarebackend.modules.blood.dto.filter;

import com.ciphertext.opencarebackend.enums.BloodGroup;
import com.ciphertext.opencarebackend.enums.Gender;
import lombok.*;

import java.util.Date;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BloodDonorFilter {
    private String name;
    private String phone;
    private List<Gender> genders;
    private List<BloodGroup> bloodGroups;
    private Integer districtId;
    private Integer upazilaId;
    private Integer unionId;
    private Integer minDonationCount;
    private Integer maxDonationCount;
    private Date lastDonationDateFrom;
    private Date lastDonationDateTo;
    private Boolean isActive;
    private String searchText; // for searching in name, phone, address
}