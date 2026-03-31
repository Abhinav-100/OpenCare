package com.ciphertext.opencarebackend.strategy;

import com.ciphertext.opencarebackend.enums.DocumentType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class DocumentUploadStrategyFactory {

    private final List<DocumentUploadStrategy> strategies;

    public DocumentUploadStrategy getStrategy(DocumentType documentType) {
        Map<DocumentType, DocumentUploadStrategy> strategyMap = strategies.stream()
                .collect(Collectors.toMap(
                        DocumentUploadStrategy::getSupportedDocumentType,
                        Function.identity()
                ));

        DocumentUploadStrategy strategy = strategyMap.get(documentType);
        if (strategy == null) {
            log.error("No strategy found for document type: {}", documentType);
            throw new IllegalArgumentException("No strategy found for document type: " + documentType);
        }
        
        return strategy;
    }
}
