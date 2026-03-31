package com.ciphertext.opencarebackend.modules.auth.repository;

import com.ciphertext.opencarebackend.modules.auth.entity.AuthUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthUserRepository extends JpaRepository<AuthUserEntity, Long> {

    Optional<AuthUserEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}