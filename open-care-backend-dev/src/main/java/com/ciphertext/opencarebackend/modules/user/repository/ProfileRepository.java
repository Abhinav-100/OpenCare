package com.ciphertext.opencarebackend.modules.user.repository;

import com.ciphertext.opencarebackend.entity.Profile;
import com.ciphertext.opencarebackend.enums.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Sadman
 */
@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long>, JpaSpecificationExecutor<Profile> {
    Optional<Profile> findByKeycloakUserId(String keycloakUserId);

    Optional<Profile> findByEmail(String email);

    Optional<Profile> findByUsername(String username);

    Long countAllByUserType(UserType userType);
}