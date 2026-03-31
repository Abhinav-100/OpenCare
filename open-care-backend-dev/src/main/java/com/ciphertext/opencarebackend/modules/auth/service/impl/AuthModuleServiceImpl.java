package com.ciphertext.opencarebackend.modules.auth.service.impl;

import com.ciphertext.opencarebackend.exception.BadRequestException;
import com.ciphertext.opencarebackend.exception.DuplicateResourceException;
import com.ciphertext.opencarebackend.modules.auth.dto.ModuleAuthResponse;
import com.ciphertext.opencarebackend.modules.auth.dto.ModuleLoginRequest;
import com.ciphertext.opencarebackend.modules.auth.dto.ModuleRegisterRequest;
import com.ciphertext.opencarebackend.modules.auth.entity.AuthRoleEntity;
import com.ciphertext.opencarebackend.modules.auth.entity.AuthUserEntity;
import com.ciphertext.opencarebackend.modules.auth.model.AuthRoleType;
import com.ciphertext.opencarebackend.modules.auth.repository.AuthRoleRepository;
import com.ciphertext.opencarebackend.modules.auth.repository.AuthUserRepository;
import com.ciphertext.opencarebackend.modules.auth.service.AuthModuleService;
import com.ciphertext.opencarebackend.modules.auth.service.ModuleJwtService;
import com.ciphertext.opencarebackend.modules.shared.dto.ModuleOverviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthModuleServiceImpl implements AuthModuleService {

    private final AuthUserRepository authUserRepository;
    private final AuthRoleRepository authRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModuleJwtService moduleJwtService;

    @Override
    @Transactional
    public ModuleAuthResponse register(ModuleRegisterRequest request) {
        if (authUserRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("User already exists with this email");
        }

        AuthRoleType requestedRole = AuthRoleType.fromInput(request.role());
        AuthRoleEntity roleEntity = authRoleRepository.findByCode(requestedRole.name())
                .orElseThrow(() -> new BadRequestException("Requested role is not available: " + requestedRole.name()));

        AuthUserEntity userEntity = new AuthUserEntity();
        userEntity.setEmail(request.email().toLowerCase());
        userEntity.setPasswordHash(passwordEncoder.encode(request.password()));
        userEntity.setFirstName(request.firstName());
        userEntity.setLastName(request.lastName());
        userEntity.setPhone(request.phone());
        userEntity.setStatus("ACTIVE");
        userEntity.setCreatedAt(LocalDateTime.now());
        userEntity.setUpdatedAt(LocalDateTime.now());
        userEntity.setRoles(Set.of(roleEntity));

        AuthUserEntity savedUser = authUserRepository.save(userEntity);
        String token = moduleJwtService.generateToken(savedUser);
        return toAuthResponse(savedUser, token);
    }

    @Override
    @Transactional(readOnly = true)
    public ModuleAuthResponse login(ModuleLoginRequest request) {
        AuthUserEntity userEntity = authUserRepository.findByEmail(request.email().toLowerCase())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), userEntity.getPasswordHash())) {
            throw new BadRequestException("Invalid email or password");
        }

        if (!"ACTIVE".equalsIgnoreCase(userEntity.getStatus())) {
            throw new BadRequestException("User account is not active");
        }

        String token = moduleJwtService.generateToken(userEntity);
        return toAuthResponse(userEntity, token);
    }

    @Override
    public ModuleOverviewResponse getOverview() {
        return new ModuleOverviewResponse(
                "auth",
                "active",
                List.of("register", "login", "jwt", "rbac")
        );
    }

    private ModuleAuthResponse toAuthResponse(AuthUserEntity userEntity, String token) {
        List<String> roles = userEntity.getRoles().stream()
                .map(AuthRoleEntity::getCode)
                .toList();

        return new ModuleAuthResponse(
                token,
                "Bearer",
                moduleJwtService.getExpirationSeconds(),
                userEntity.getEmail(),
                roles
        );
    }
}