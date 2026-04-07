package com.ciphertext.opencarebackend.modules.auth.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
/**
 * Flow note: TokenResponse belongs to the authentication module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public class TokenResponse {
    private String access_token;
    private String refresh_token;
    private String token_type;
    private int expires_in;
    private String scope;
}