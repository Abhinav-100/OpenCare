package com.ciphertext.opencarebackend.strategy;

import com.ciphertext.opencarebackend.enums.DocumentType;

public interface DocumentUploadStrategy {
    void updateEntity(Long entityId, String objectName);
    DocumentType getSupportedDocumentType();
}
