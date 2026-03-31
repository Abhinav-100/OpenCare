package com.ciphertext.opencarebackend.modules.provider.dto.filter;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AmenityFilter {
    private String name;
    private Integer hospitalId;
    private String type;
    private Integer minPrice;
    private Integer maxPrice;
    private Integer available;
}