package com.ciphertext.opencarebackend.modules.auth.repository;

import com.ciphertext.opencarebackend.modules.auth.entity.AuthRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Flow note: AuthRoleRepository belongs to the authentication module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public interface AuthRoleRepository extends JpaRepository<AuthRoleEntity, Long> {

    Optional<AuthRoleEntity> findByCode(String code);

    boolean existsByCode(String code);
}