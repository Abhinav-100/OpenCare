package com.ciphertext.opencarebackend.modules.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.module-jwt")
/**
 * Flow note: ModuleJwtProperties belongs to the authentication module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public record ModuleJwtProperties(
        String secret,
        String issuer,
        long expirationSeconds
) {
}