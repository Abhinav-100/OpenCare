package com.ciphertext.opencarebackend.modules.shared.service;

import com.ciphertext.opencarebackend.exception.BadRequestException;
import com.ciphertext.opencarebackend.exception.ResourceNotFoundException;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MinioService {

    private static final long MULTIPART_MIN_SIZE = 5L * 1024L * 1024L;

    private final MinioClient minioClient;

    @Value("${minio.bucketName}")
    private String bucketName;

    private final Object bucketLock = new Object();
    private volatile boolean bucketInitialized;

    /**
     * Upload a file to MinIO
     *
     * @param file The file to upload
     * @return The object name (path) in MinIO
     */
    public String uploadFile(MultipartFile file) {
        validateFile(file);
        ensureBucketExists();

        String objectName = UUID.randomUUID() + extractExtension(file.getOriginalFilename());
        String contentType = resolveContentType(file.getContentType());

        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(contentType)
                            .build());

            return objectName;
        } catch (MinioException me) {
            throw new RuntimeException("MinIO error uploading file: " + me.getMessage(), me);
        } catch (IOException ioe) {
            throw new RuntimeException("I/O error uploading file: " + ioe.getMessage(), ioe);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error uploading file to MinIO", e);
        }
    }

    /**
     * Upload a file to MinIO with a specific object name
     *
     * @param file The file to upload
     * @param directory The object directory in MinIO
     */
    public String uploadFile(MultipartFile file, String directory) {
        validateFile(file);
        String normalizedDirectory = normalizeDirectory(directory);
        ensureBucketExists();

        String objectName = normalizedDirectory + "/" + UUID.randomUUID() + extractExtension(file.getOriginalFilename());
        String contentType = resolveContentType(file.getContentType());

        try (InputStream in = file.getInputStream()) {
            long size = file.getSize();
            if (size <= 0) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(objectName)
                                .stream(in, -1, MULTIPART_MIN_SIZE)
                                .contentType(contentType)
                                .build()
                );
            } else {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(objectName)
                                .stream(in, size, MULTIPART_MIN_SIZE)
                                .contentType(contentType)
                                .build()
                );
            }

            return objectName;
        } catch (MinioException me) {
            throw new RuntimeException("MinIO error uploading file: " + me.getMessage(), me);
        } catch (IOException ioe) {
            throw new RuntimeException("I/O error uploading file: " + ioe.getMessage(), ioe);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error uploading file to MinIO", e);
        }
    }

    /**
     * Download a file from MinIO
     *
     * @param objectName The object name in MinIO
     * @return Input stream of the file
     */
    public InputStream downloadFile(String objectName) {
        validateObjectName(objectName);
        ensureBucketExists();

        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build());
        } catch (ErrorResponseException e) {
            if (isNotFound(e)) {
                throw new ResourceNotFoundException("File not found: " + objectName);
            }
            throw new RuntimeException("MinIO error downloading file: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error downloading file from MinIO", e);
        }
    }

    /**
     * Delete a file from MinIO
     *
     * @param objectName The object name in MinIO
     */
    public void deleteFile(String objectName) {
        validateObjectName(objectName);
        ensureBucketExists();

        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build());
        } catch (ErrorResponseException e) {
            if (isNotFound(e)) {
                throw new ResourceNotFoundException("File not found: " + objectName);
            }
            throw new RuntimeException("MinIO error deleting file: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting file from MinIO", e);
        }
    }

    /**
     * List all files in a bucket
     *
     * @return List of object names
     */
    public List<String> listFiles() {
        ensureBucketExists();
        List<String> files = new ArrayList<>();
        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName)
                            .build());

            for (Result<Item> result : results) {
                files.add(result.get().objectName());
            }
            return files;
        } catch (Exception e) {
            throw new RuntimeException("Error listing files from MinIO", e);
        }
    }

    /**
     * Get pre-signed URL for temporary access to a file
     *
     * @param objectName The object name in MinIO
     * @param expirySeconds How long the URL should be valid
     * @return Pre-signed URL
     */
    public String getPresignedUrl(String objectName, int expirySeconds) {
        validateObjectName(objectName);
        if (expirySeconds <= 0) {
            throw new BadRequestException("Expiry seconds must be a positive number");
        }
        ensureBucketExists();

        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry(expirySeconds)
                            .method(Method.GET)
                            .build());
        } catch (ErrorResponseException e) {
            if (isNotFound(e)) {
                throw new ResourceNotFoundException("File not found: " + objectName);
            }
            throw new RuntimeException("MinIO error generating pre-signed URL: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error generating pre-signed URL", e);
        }
    }

    private void ensureBucketExists() {
        if (bucketInitialized) {
            return;
        }
        synchronized (bucketLock) {
            if (bucketInitialized) {
                return;
            }
            try {
                boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder()
                        .bucket(bucketName)
                        .build());

                if (!bucketExists) {
                    minioClient.makeBucket(MakeBucketArgs.builder()
                            .bucket(bucketName)
                            .build());
                }
                bucketInitialized = true;
            } catch (Exception e) {
                throw new RuntimeException("Error initializing MinIO bucket", e);
            }
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File must not be empty");
        }
    }

    private void validateObjectName(String objectName) {
        if (!StringUtils.hasText(objectName)) {
            throw new BadRequestException("Object name is required");
        }
    }

    private String normalizeDirectory(String directory) {
        if (!StringUtils.hasText(directory)) {
            throw new BadRequestException("Directory is required");
        }
        String normalized = directory.trim();
        while (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }
        while (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        if (!StringUtils.hasText(normalized)) {
            throw new BadRequestException("Directory is required");
        }
        return normalized;
    }

    private String extractExtension(String filename) {
        String extension = StringUtils.getFilenameExtension(filename);
        return StringUtils.hasText(extension) ? "." + extension : "";
    }

    private String resolveContentType(String contentType) {
        return StringUtils.hasText(contentType) ? contentType : "application/octet-stream";
    }

    private boolean isNotFound(ErrorResponseException e) {
        String code = e.errorResponse() != null ? e.errorResponse().code() : null;
        return "NoSuchKey".equals(code) || "NoSuchObject".equals(code) || "NoSuchBucket".equals(code);
    }
}