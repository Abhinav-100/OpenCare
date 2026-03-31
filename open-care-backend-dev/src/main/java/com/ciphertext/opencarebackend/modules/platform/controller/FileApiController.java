package com.ciphertext.opencarebackend.modules.platform.controller;

import com.ciphertext.opencarebackend.enums.DocumentType;
import com.ciphertext.opencarebackend.modules.platform.service.FileOrchestrationService;
import com.ciphertext.opencarebackend.modules.shared.service.MinioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.Positive;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Validated
@Tag(name = "File Management", description = "API for uploading, downloading, listing, deleting, and generating presigned URLs for files")
public class FileApiController {

    private final MinioService minioService;
    private final FileOrchestrationService fileOrchestrationService;

    @Operation(
            summary = "Upload a file with document type and entity ID",
            description = "Uploads a file to MinIO and updates the related entity based on document type. The file will be saved at: {documentType}/{entityId}",
            parameters = {
                    @Parameter(name = "documentType", description = "Type of document (e.g., PROFILE_PICTURE, LOGO, etc.)"),
                    @Parameter(name = "entityId", description = "Associated entity ID (e.g., Profile ID)"),
                    @Parameter(name = "file", description = "Multipart file to upload")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "File uploaded successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
            }
    )
    @PostMapping(value = "/{documentType}/id/{entityId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadFile(
            @PathVariable DocumentType documentType,
            @PathVariable @Positive Long entityId,
            @RequestParam("file") MultipartFile file) {

        Map<String, String> response = fileOrchestrationService.uploadAndSave(file, documentType, entityId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Upload a file",
            description = "Uploads a file to the default location and returns metadata including object name.",
            parameters = {
                    @Parameter(name = "file", description = "Multipart file to upload")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "File uploaded successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
            }
    )
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        String objectName = minioService.uploadFile(file);
        String url = minioService.getPresignedUrl(objectName, 3600);

        Map<String, String> response = new HashMap<>();
        response.put("photoUrl", objectName);
        response.put("presignedUrl", url);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Download a file by object name",
            description = "Streams the file content as an attachment for the given object name.",
            parameters = {
                    @Parameter(name = "objectName", description = "Object name/key of the file to download")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "File stream",
                            content = @Content(mediaType = "application/octet-stream",
                                    schema = @Schema(type = "string", format = "binary"))),
                    @ApiResponse(responseCode = "404", description = "File not found", content = @Content)
            }
    )
    @GetMapping("/download/{objectName}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String objectName) {
        InputStream inputStream = minioService.downloadFile(objectName);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", objectName);

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(inputStream));
    }

    @Operation(
            summary = "Delete a file by object name",
            description = "Deletes the file for the given object name and returns a confirmation message.",
            parameters = {
                    @Parameter(name = "objectName", description = "Object name/key of the file to delete")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "File deleted",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
                    @ApiResponse(responseCode = "404", description = "File not found", content = @Content)
            }
    )
    @DeleteMapping("/{objectName}")
    public ResponseEntity<Map<String, String>> deleteFile(@PathVariable String objectName) {
        minioService.deleteFile(objectName);

        Map<String, String> response = new HashMap<>();
        response.put("message", "File deleted successfully");
        response.put("objectName", objectName);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "List files",
            description = "Lists all object names available.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of object names",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
            }
    )
    @GetMapping("/list")
    public ResponseEntity<List<String>> listFiles() {
        return ResponseEntity.ok(minioService.listFiles());
    }

    @Operation(
            summary = "Generate a presigned URL for an object",
            description = "Generates a temporary presigned URL for accessing the object.",
            parameters = {
                    @Parameter(name = "objectName", description = "Object name/key"),
                    @Parameter(name = "expirySeconds", description = "URL expiry time in seconds, default 3600")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Presigned URL generated",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
                    @ApiResponse(responseCode = "404", description = "File not found", content = @Content)
            }
    )
    @GetMapping("/url/{objectName}")
    public ResponseEntity<Map<String, String>> getPresignedUrl(
            @PathVariable String objectName,
            @RequestParam(defaultValue = "3600") @Positive int expirySeconds) {

        String url = minioService.getPresignedUrl(objectName, expirySeconds);

        Map<String, String> response = new HashMap<>();
        response.put("url", url);
        response.put("expirySeconds", String.valueOf(expirySeconds));

        return ResponseEntity.ok(response);
    }
}