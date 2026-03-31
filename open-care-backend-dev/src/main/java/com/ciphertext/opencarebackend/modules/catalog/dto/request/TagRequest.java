package com.ciphertext.opencarebackend.modules.catalog.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagRequest {
    private Long id;

    @NotBlank(message = "Tag name is required")
    @Size(max = 100, message = "Tag name must be less than 100 characters")
    private String name;

    @Size(max = 100, message = "Display name must be less than 100 characters")
    private String displayName;

    @NotBlank(message = "Category is required")
    @Size(max = 50, message = "Category must be less than 50 characters")
    private String category;
}