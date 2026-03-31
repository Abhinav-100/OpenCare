package com.ciphertext.opencarebackend.modules.provider.dto.filter;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
@Builder
@Getter
@Setter
public class MedicalTestFilter {
    private String  name;
    private Integer hospitalId;
    private Integer medicalTestId;
    private Integer parentMedicalTestId;
    private String category;
    private Integer minPrice;
    private Integer maxPrice;
    private Integer available;


}