package com.ciphertext.opencarebackend.modules.blood.dto.filter;

import com.ciphertext.opencarebackend.enums.BloodComponent;
import com.ciphertext.opencarebackend.enums.BloodGroup;
import com.ciphertext.opencarebackend.enums.Gender;
import com.ciphertext.opencarebackend.enums.RequisitionStatus;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BloodRequisitionFilter {
    private Long requesterId;
    private String patientName;
    private Integer minPatientAge;
    private Integer maxPatientAge;
    private List<Gender> patientGenders;
    private List<BloodGroup> bloodGroups;
    private List<BloodComponent> bloodComponents;
    private Integer minQuantityBags;
    private Integer maxQuantityBags;
    private LocalDate neededByDateFrom;
    private LocalDate neededByDateTo;
    private Integer hospitalId;
    private String contactPhone;
    private Integer districtId;
    private Integer upazilaId;
    private List<RequisitionStatus> statuses;
    private LocalDate fulfilledDateFrom;
    private LocalDate fulfilledDateTo;
    private Boolean isUrgent; // for requests needed within 24-48 hours
    private String searchText; // for searching in patient name, contact person, description
}