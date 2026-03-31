package com.ciphertext.opencarebackend.modules.provider.dto.filter;

import com.ciphertext.opencarebackend.enums.Country;
import com.ciphertext.opencarebackend.enums.SocialOrganizationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialOrganizationFilter {
    private String name;
    private String bnName;
    private String phone;
    private String email;
    private SocialOrganizationType socialOrganizationType;
    private String address;
    private Country originCountry;
}