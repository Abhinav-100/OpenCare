package com.ciphertext.opencarebackend.util;

import com.ciphertext.opencarebackend.modules.shared.service.MinioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MinioUtil {
    private final MinioService minioService;
    private static final int DEFAULT_EXPIRY_SECONDS = 3600;

    public String getPresignedUrl(String objectName) {
        if (objectName == null || objectName.isEmpty()) {
            return null;
        }
        return minioService.getPresignedUrl(objectName, DEFAULT_EXPIRY_SECONDS);
    }

    public String getPresignedUrl(String objectName, int expirySeconds) {
        if (objectName == null || objectName.isEmpty()) {
            return null;
        }
        return minioService.getPresignedUrl(objectName, expirySeconds);
    }
}
