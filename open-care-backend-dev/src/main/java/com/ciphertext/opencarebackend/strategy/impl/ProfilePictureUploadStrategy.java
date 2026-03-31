package com.ciphertext.opencarebackend.strategy.impl;

import com.ciphertext.opencarebackend.entity.Profile;
import com.ciphertext.opencarebackend.enums.DocumentType;
import com.ciphertext.opencarebackend.exception.BadRequestException;
import com.ciphertext.opencarebackend.exception.ResourceNotFoundException;
import com.ciphertext.opencarebackend.modules.user.repository.ProfileRepository;
import com.ciphertext.opencarebackend.strategy.DocumentUploadStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProfilePictureUploadStrategy implements DocumentUploadStrategy {

    private final ProfileRepository profileRepository;

    @Override
    public void updateEntity(Long entityId, String objectName) {
        log.info("Updating profile picture for profile id: {}", entityId);
        if (entityId == null || entityId <= 0) {
            throw new BadRequestException("Profile ID must be positive");
        }

        Profile profile = profileRepository.findById(entityId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found with id: " + entityId));

        profile.setImageUrl(objectName);
        profileRepository.save(profile);

        log.info("Profile picture updated successfully for profile id: {}", entityId);
    }

    @Override
    public DocumentType getSupportedDocumentType() {
        return DocumentType.PROFILE_PICTURE;
    }
}
