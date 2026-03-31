package com.ciphertext.opencarebackend.modules.shared.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnionResponse {
    private int id;
    private UpazilaResponse upazila;
    private String name;
    private String bnName;
    private String url;
}