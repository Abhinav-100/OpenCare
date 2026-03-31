package com.ciphertext.opencarebackend.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    private final JwtDecoder jwtDecoder;

    public JwtUtil(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    public String extractUserId(String token) {
        Jwt jwt = jwtDecoder.decode(token);
        return jwt.getSubject();
    }

    public String extractUsername(String token) {
        Jwt jwt = jwtDecoder.decode(token);
        return (String) jwt.getClaims().get("preferred_username");
    }

    public List<String> extractRoles(String token) {
        Jwt jwt = jwtDecoder.decode(token);
        Object realmAccessClaim = jwt.getClaims().get("realm_access");
        if (realmAccessClaim instanceof Map<?, ?> realmAccessMap) {
            Object rolesClaim = realmAccessMap.get("roles");
            if (rolesClaim instanceof List<?> roles) {
                return roles.stream()
                        .filter(String.class::isInstance)
                        .map(String.class::cast)
                        .toList();
            }
        }
        return Collections.emptyList();
    }

    public boolean validateToken(String token) {
        try {
            jwtDecoder.decode(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
