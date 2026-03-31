package com.ciphertext.opencarebackend.modules.auth.service.impl;

import com.ciphertext.opencarebackend.modules.auth.config.ModuleJwtProperties;
import com.ciphertext.opencarebackend.modules.auth.entity.AuthUserEntity;
import com.ciphertext.opencarebackend.modules.auth.service.ModuleJwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ModuleJwtServiceImpl implements ModuleJwtService {

    @Qualifier("moduleJwtEncoder")
    private final JwtEncoder jwtEncoder;

    @Qualifier("moduleJwtDecoder")
    private final JwtDecoder jwtDecoder;

    private final ModuleJwtProperties moduleJwtProperties;

    @Override
    public String generateToken(AuthUserEntity user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(moduleJwtProperties.expirationSeconds());

        List<String> roles = user.getRoles().stream()
                .map(role -> role.getCode())
                .toList();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(moduleJwtProperties.issuer())
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(String.valueOf(user.getId()))
                .claim("email", user.getEmail())
                .claim("roles", roles)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    @Override
    public Jwt decodeToken(String token) {
        return jwtDecoder.decode(token);
    }

    @Override
    public long getExpirationSeconds() {
        return moduleJwtProperties.expirationSeconds();
    }
}