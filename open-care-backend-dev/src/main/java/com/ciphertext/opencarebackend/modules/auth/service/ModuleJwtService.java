package com.ciphertext.opencarebackend.modules.auth.service;

import com.ciphertext.opencarebackend.modules.auth.entity.AuthUserEntity;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Flow note: ModuleJwtService belongs to the authentication module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public interface ModuleJwtService {

    String generateToken(AuthUserEntity user);

    Jwt decodeToken(String token);

    long getExpirationSeconds();
}