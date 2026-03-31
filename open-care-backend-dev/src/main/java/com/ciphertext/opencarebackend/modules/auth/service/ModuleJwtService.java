package com.ciphertext.opencarebackend.modules.auth.service;

import com.ciphertext.opencarebackend.modules.auth.entity.AuthUserEntity;
import org.springframework.security.oauth2.jwt.Jwt;

public interface ModuleJwtService {

    String generateToken(AuthUserEntity user);

    Jwt decodeToken(String token);

    long getExpirationSeconds();
}