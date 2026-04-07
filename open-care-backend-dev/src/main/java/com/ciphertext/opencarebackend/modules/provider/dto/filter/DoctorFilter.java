package com.ciphertext.opencarebackend.modules.provider.dto.filter;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
/**
 * Flow note: DoctorFilter belongs to the provider doctor/hospital module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public class DoctorFilter {
    private String name;
    private String bnName;
    private String bmdcNo;
    private String speciality;
    private Integer minExperience;
    private Integer maxExperience;
    private Boolean isVerified;
    private Boolean isActive;
    private Boolean isCurrentWorkplace;
    private Integer districtId;
    private Integer upazilaId;
    private Integer unionId;
    private List<Integer> districtIds;
    private List<Integer> upazilaIds;
    private List<Integer> unionIds;
    private Integer specialityId;
    private List<Integer> specialityIds;
    private Integer associationId;
    private Integer degreeId;
    private List<Integer> degreeIds;
    private Integer hospitalId;
    private List<Integer> hospitalIds;
    private Integer workInstitutionId;
    private List<Integer> institutionIds;
    private Integer studyInstitutionId;
}