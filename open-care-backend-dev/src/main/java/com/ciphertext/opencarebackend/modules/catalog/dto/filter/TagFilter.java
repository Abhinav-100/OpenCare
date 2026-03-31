package com.ciphertext.opencarebackend.modules.catalog.dto.filter;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagFilter {
    private String name;
    private String category;
    private String query; // for searching in name or displayName
}