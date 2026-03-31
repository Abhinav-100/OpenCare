package com.ciphertext.opencarebackend.modules.platform.service;

import com.ciphertext.opencarebackend.enums.DocumentType;
import com.ciphertext.opencarebackend.exception.BadRequestException;
import com.ciphertext.opencarebackend.strategy.DocumentUploadStrategy;
import com.ciphertext.opencarebackend.strategy.DocumentUploadStrategyFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import com.ciphertext.opencarebackend.modules.shared.service.MinioService;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FileOrchestrationService {

    private final MinioService minioService;
    private final DocumentUploadStrategyFactory strategyFactory;

    public Map<String, String> uploadAndSave(
            MultipartFile file,
            DocumentType documentType,
            Long entityId) {

        if (documentType == null) {
            throw new BadRequestException("Document type is required");
        }
        if (entityId == null || entityId <= 0) {
            throw new BadRequestException("Entity ID must be positive");
        }
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File must not be empty");
        }

        log.info("Starting file upload for documentType: {}, entityId: {}", documentType, entityId);

        // 1. Upload to MinIO
        String path = documentType.name().toLowerCase();
        String objectName = minioService.uploadFile(file, path);
        log.info("File uploaded to MinIO: {}", objectName);

        // 2. Update related entity using strategy pattern
        DocumentUploadStrategy strategy = strategyFactory.getStrategy(documentType);
        strategy.updateEntity(entityId, objectName);
        log.info("Entity updated successfully");

        // 3. Generate presigned URL
        String presignedUrl = minioService.getPresignedUrl(objectName, 3600);

        // 4. Build response
        Map<String, String> response = new HashMap<>();
        response.put("photoUrl", objectName);
        response.put("presignedUrl", presignedUrl);
        response.put("documentType", documentType.name());

        log.info("File upload completed successfully");
        return response;
    }
}