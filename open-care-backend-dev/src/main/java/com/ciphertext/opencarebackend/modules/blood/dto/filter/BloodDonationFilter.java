package com.ciphertext.opencarebackend.modules.blood.dto.filter;

import com.ciphertext.opencarebackend.enums.BloodComponent;
import com.ciphertext.opencarebackend.enums.BloodGroup;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BloodDonationFilter {
    private Long donorId;
    private Integer hospitalId;
    private LocalDate donationDateFrom;
    private LocalDate donationDateTo;
    private List<BloodGroup> bloodGroups;
    private List<BloodComponent> bloodComponents;
    private Integer minQuantityMl;
    private Integer maxQuantityMl;
    private Integer districtId;
    private Integer upazilaId;
}