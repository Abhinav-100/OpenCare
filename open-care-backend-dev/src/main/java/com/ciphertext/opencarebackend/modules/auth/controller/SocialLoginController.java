package com.ciphertext.opencarebackend.modules.auth.controller;
import com.ciphertext.opencarebackend.modules.auth.dto.response.TokenResponse;
import com.ciphertext.opencarebackend.modules.auth.service.KeycloakService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/social")
@Tag(name = "Social Login", description = "OAuth/social login endpoints for redirect and callback handling")
public class SocialLoginController {

    @Value("${app.keycloak.server-url}")
    private String keycloakServerUrl;

    @Value("${app.keycloak.realm}")
    private String realm;

    @Value("${app.keycloak.client-id}")
    private String clientId;

    @Value("${app.frontend.redirect-uri}")
    private String frontendRedirectUri;

    private final KeycloakService keycloakService;

    @GetMapping("/redirect/{provider}")
    @Operation(summary = "Get social provider authorization URL", description = "Returns an authorization URL to redirect users to the social provider login page via Keycloak")
    public ResponseEntity<Map<String, String>> getAuthorizationUrl(@PathVariable String provider) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(keycloakServerUrl)
                .path("/realms/")
                .path(realm)
                .path("/protocol/openid-connect/auth")
                .queryParam("client_id", clientId)
                .queryParam("response_type", "code")
                .queryParam("redirect_uri", frontendRedirectUri)
                .queryParam("scope", "openid")
                .queryParam("kc_idp_hint", provider);

        String authUrl = builder.build()
                .encode()
                .toUriString();

        Map<String, String> response = new HashMap<>();
        response.put("authorizationUrl", authUrl);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/callback")
    @Operation(summary = "Handle social login callback", description = "Handles the OAuth callback and exchanges the authorization code for tokens")
    public ResponseEntity<TokenResponse> handleCallback(
            @RequestParam("code") String code,
            @RequestParam("session_state") String sessionState) {

        // Exchange authorization code for token
        TokenResponse tokenResponse = keycloakService.exchangeCodeForToken(code).block();
        return ResponseEntity.ok(tokenResponse);
    }
}
