package com.ciphertext.opencarebackend.modules.provider.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AssociationRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @Size(max = 100, message = "Bangla name must not exceed 100 characters")
    private String bnName;

    @Size(max = 50, message = "Short name must not exceed 50 characters")
    private String shortName;

    @NotBlank(message = "Association type is required")
    private String associationType;
    private Integer medicalSpecialityId;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @Size(max = 255, message = "Logo URL must not exceed 255 characters")
    private String logoUrl;
    private LocalDate foundedDate;

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

    private Integer divisionId;
    private Integer districtId;
    private Integer upazilaId;

    private String originCountry;

    private String domain;

    private Boolean isAffiliated;
    private Boolean isActive;
}