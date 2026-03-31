package com.ciphertext.opencarebackend.modules.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.module-jwt")
public record ModuleJwtProperties(
        String secret,
        String issuer,
        long expirationSeconds
) {
}