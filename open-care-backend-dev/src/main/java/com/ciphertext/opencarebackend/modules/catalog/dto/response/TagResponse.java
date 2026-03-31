package com.ciphertext.opencarebackend.modules.catalog.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagResponse {
    private Integer id;
    private String name;
    private String displayName;
    private String category;
}