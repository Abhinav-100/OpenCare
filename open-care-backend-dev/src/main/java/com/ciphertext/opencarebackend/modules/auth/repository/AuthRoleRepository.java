package com.ciphertext.opencarebackend.modules.auth.repository;

import com.ciphertext.opencarebackend.modules.auth.entity.AuthRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthRoleRepository extends JpaRepository<AuthRoleEntity, Long> {

    Optional<AuthRoleEntity> findByCode(String code);

    boolean existsByCode(String code);
}