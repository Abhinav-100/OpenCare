package com.ciphertext.opencarebackend.modules.provider.dto.filter;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
/**
 * Flow note: HospitalFilter belongs to the provider doctor/hospital module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public class HospitalFilter {
    private String name;
    private String bnName;
    private Integer numberOfBed;
    private Integer districtId;
    private Integer upazilaId;
    private Integer unionId;
    private List<String> hospitalTypes;
    private String organizationType;
    private Boolean isActive;
    private Boolean hasEmergencyService;
    private Boolean hasAmbulanceService;
    private Boolean hasBloodBank;
}