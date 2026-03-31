package com.ciphertext.opencarebackend.modules.shared.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpazilaResponse {
    private int id;
    private DistrictResponse district;
    private String name;
    private String bnName;
    private String url;
}