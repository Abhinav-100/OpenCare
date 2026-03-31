package com.ciphertext.opencarebackend.modules.auth.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TokenResponse {
    private String access_token;
    private String refresh_token;
    private String token_type;
    private int expires_in;
    private String scope;
}