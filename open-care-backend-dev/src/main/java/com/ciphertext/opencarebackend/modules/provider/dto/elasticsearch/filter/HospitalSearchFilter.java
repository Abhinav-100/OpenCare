package com.ciphertext.opencarebackend.modules.provider.dto.elasticsearch.filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
/**
 * Flow note: HospitalSearchFilter belongs to the provider doctor/hospital module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public class HospitalSearchFilter {
    private String searchTerm;
    private Integer districtId;
    private Integer upazilaId;
    private String hospitalType;
}