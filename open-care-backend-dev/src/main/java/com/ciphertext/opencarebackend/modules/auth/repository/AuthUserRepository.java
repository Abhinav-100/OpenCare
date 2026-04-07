package com.ciphertext.opencarebackend.modules.auth.repository;

import com.ciphertext.opencarebackend.modules.auth.entity.AuthUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Flow note: AuthUserRepository belongs to the authentication module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public interface AuthUserRepository extends JpaRepository<AuthUserEntity, Long> {

    Optional<AuthUserEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}