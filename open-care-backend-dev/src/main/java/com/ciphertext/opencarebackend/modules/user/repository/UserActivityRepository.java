package com.ciphertext.opencarebackend.modules.user.repository;

import com.ciphertext.opencarebackend.entity.UserActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserActivityRepository extends JpaRepository<UserActivity, Long>, JpaSpecificationExecutor<UserActivity> {
    Optional<UserActivity> findByProfileId(Long profileId);
}
