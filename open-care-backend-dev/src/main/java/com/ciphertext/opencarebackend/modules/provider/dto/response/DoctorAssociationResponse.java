package com.ciphertext.opencarebackend.modules.provider.dto.response;
import com.ciphertext.opencarebackend.modules.shared.dto.response.enums.MembershipTypeResponse;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
/**
 * Flow note: DoctorAssociationResponse belongs to the provider doctor/hospital module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public class DoctorAssociationResponse {
    private Long id;
    private DoctorResponse doctor;
    private AssociationResponse association;
    private MembershipTypeResponse membershipType;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isActive;
}
