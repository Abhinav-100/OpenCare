package com.ciphertext.opencarebackend.modules.provider.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialOrganizationRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    @Size(max = 255, message = "Bangla name must not exceed 255 characters")
    private String bnName;

    @NotBlank(message = "Social organization type is required")
    private String socialOrganizationType;

    private LocalDateTime foundedDate;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;

    @Size(max = 255, message = "Website URL must not exceed 255 characters")
    private String websiteUrl;

    @Size(max = 255, message = "Facebook URL must not exceed 255 characters")
    private String facebookUrl;

    @Size(max = 255, message = "Twitter URL must not exceed 255 characters")
    private String twitterUrl;

    @Size(max = 255, message = "LinkedIn URL must not exceed 255 characters")
    private String linkedinUrl;

    @Size(max = 255, message = "YouTube URL must not exceed 255 characters")
    private String youtubeUrl;

    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @Size(max = 20, message = "Phone must not exceed 20 characters")
    private String phone;

    private String originCountry;

    private Set<Integer> tagIds; // Tag IDs to associate with the institution
}