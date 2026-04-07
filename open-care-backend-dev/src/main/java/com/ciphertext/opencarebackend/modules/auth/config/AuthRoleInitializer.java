package com.ciphertext.opencarebackend.modules.auth.config;

import com.ciphertext.opencarebackend.modules.auth.entity.AuthRoleEntity;
import com.ciphertext.opencarebackend.modules.auth.model.AuthRoleType;
import com.ciphertext.opencarebackend.modules.auth.repository.AuthRoleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
/**
 * Flow note: AuthRoleInitializer belongs to the authentication module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public class AuthRoleInitializer {

    private final AuthRoleRepository authRoleRepository;

    @PostConstruct
    @Transactional
    public void initializeRoles() {
        for (AuthRoleType roleType : AuthRoleType.values()) {
            authRoleRepository.findByCode(roleType.name())
                    .orElseGet(() -> {
                        AuthRoleEntity roleEntity = new AuthRoleEntity();
                        roleEntity.setCode(roleType.name());
                        roleEntity.setName(roleType.name());
                        roleEntity.setDescription("System role " + roleType.name());
                        roleEntity.setIsActive(true);
                        roleEntity.setCreatedAt(LocalDateTime.now());
                        roleEntity.setUpdatedAt(LocalDateTime.now());
                        return authRoleRepository.save(roleEntity);
                    });
        }
    }
}