package com.ciphertext.opencarebackend.modules.provider.dto.response;
import com.ciphertext.opencarebackend.modules.catalog.dto.response.enums.CountryResponse;
import com.ciphertext.opencarebackend.modules.provider.dto.response.enums.SocialOrganizationTypeResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import com.ciphertext.opencarebackend.modules.catalog.dto.response.TagResponse;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialOrganizationResponse {
    private Integer id;
    private String name;
    private String bnName;
    private SocialOrganizationTypeResponse socialOrganizationType;
    private LocalDateTime foundedDate;
    private String description;
    private String address;
    private String websiteUrl;
    private String facebookUrl;
    private String twitterUrl;
    private String linkedinUrl;
    private String youtubeUrl;
    private String email;
    private String phone;
    private CountryResponse originCountry;
    private Set<TagResponse> tags;
}
