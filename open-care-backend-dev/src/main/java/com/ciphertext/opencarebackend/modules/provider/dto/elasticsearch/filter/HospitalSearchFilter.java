package com.ciphertext.opencarebackend.modules.provider.dto.elasticsearch.filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HospitalSearchFilter {
    private String searchTerm;
    private Integer districtId;
    private Integer upazilaId;
    private String hospitalType;
}