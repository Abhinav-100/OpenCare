package com.ciphertext.opencarebackend.modules.blood.dto.response;
import com.ciphertext.opencarebackend.modules.blood.dto.response.enums.BloodComponentResponse;
import com.ciphertext.opencarebackend.modules.blood.dto.response.enums.BloodGroupResponse;
import com.ciphertext.opencarebackend.modules.shared.dto.response.enums.GenderResponse;
import com.ciphertext.opencarebackend.modules.blood.dto.response.enums.RequisitionStatusResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.ciphertext.opencarebackend.modules.provider.dto.response.HospitalResponse;
import com.ciphertext.opencarebackend.modules.shared.dto.response.DistrictResponse;
import com.ciphertext.opencarebackend.modules.shared.dto.response.UpazilaResponse;
import com.ciphertext.opencarebackend.modules.user.dto.response.ProfileResponse;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BloodRequisitionResponse {
    private Long id;
    private ProfileResponse requester;
    private String patientName;
    private Integer patientAge;
    private GenderResponse patientGender;
    private BloodGroupResponse bloodGroup;
    private BloodComponentResponse bloodComponent;
    private Integer quantityBags;
    private LocalDate neededByDate;
    private HospitalResponse hospital;
    private String contactPerson;
    private String contactPhone;
    private String description;
    private DistrictResponse district;
    private UpazilaResponse upazila;
    private BigDecimal lat;
    private BigDecimal lon;
    private RequisitionStatusResponse status;
    private LocalDate fulfilledDate;
}