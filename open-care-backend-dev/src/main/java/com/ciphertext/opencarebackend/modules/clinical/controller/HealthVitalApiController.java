package com.ciphertext.opencarebackend.modules.clinical.controller;
import com.ciphertext.opencarebackend.modules.clinical.dto.request.HealthVitalRequest;
import com.ciphertext.opencarebackend.modules.clinical.dto.response.HealthVitalResponse;
import com.ciphertext.opencarebackend.modules.clinical.dto.response.LatestHealthVitalProjection;
import com.ciphertext.opencarebackend.entity.HealthVital;
import com.ciphertext.opencarebackend.mapper.HealthVitalMapper;
import com.ciphertext.opencarebackend.modules.clinical.service.HealthVitalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/health-vitals")
@RequiredArgsConstructor
@Tag(name = "Health Vital Management", description = "API for managing health vitals and tracking health metrics")
public class HealthVitalApiController {

    private final HealthVitalService healthVitalService;
    private final HealthVitalMapper healthVitalMapper;

    @Operation(
            summary = "Get current user's health vitals",
            description = "Retrieves paginated health vitals for the authenticated user"
    )
    @GetMapping("/self")
    public ResponseEntity<Map<String, Object>> getSelfHealthVitals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "recordedAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            Authentication authentication) {

        Jwt jwt = (Jwt) authentication.getPrincipal();
        String keycloakUserId = jwt.getSubject();

        log.info("Fetching health vitals for current user: {}", keycloakUserId);

        Sort.Direction direction = sortDirection.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<HealthVital> healthVitalsPage = healthVitalService.getHealthVitalsByKeycloakUserId(keycloakUserId, pageable);
        Page<HealthVitalResponse> responsePage = healthVitalsPage.map(healthVitalMapper::toResponse);

        Map<String, Object> response = new HashMap<>();
        response.put("healthVitals", responsePage.getContent());
        response.put("currentPage", responsePage.getNumber());
        response.put("totalItems", responsePage.getTotalElements());
        response.put("totalPages", responsePage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get all health vitals with pagination",
            description = "Retrieves a paginated list of all health vitals"
    )
    @GetMapping("")
    public ResponseEntity<Map<String, Object>> getAllHealthVitals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "recordedAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        log.info("Fetching all health vitals - page: {}, size: {}", page, size);

        Sort.Direction direction = sortDirection.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<HealthVital> healthVitalsPage = healthVitalService.getAllHealthVitals(pageable);
        Page<HealthVitalResponse> responsePage = healthVitalsPage.map(healthVitalMapper::toResponse);

        Map<String, Object> response = new HashMap<>();
        response.put("healthVitals", responsePage.getContent());
        response.put("currentPage", responsePage.getNumber());
        response.put("totalItems", responsePage.getTotalElements());
        response.put("totalPages", responsePage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get health vitals by profile ID",
            description = "Retrieves paginated health vitals for a specific profile"
    )
    @GetMapping("/profile/{profileId}")
    public ResponseEntity<Map<String, Object>> getHealthVitalsByProfileId(
            @PathVariable Long profileId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "recordedAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        log.info("Fetching health vitals for profile ID: {}", profileId);

        Sort.Direction direction = sortDirection.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<HealthVital> healthVitalsPage = healthVitalService.getHealthVitalsByProfileId(profileId, pageable);
        Page<HealthVitalResponse> responsePage = healthVitalsPage.map(healthVitalMapper::toResponse);

        Map<String, Object> response = new HashMap<>();
        response.put("healthVitals", responsePage.getContent());
        response.put("currentPage", responsePage.getNumber());
        response.put("totalItems", responsePage.getTotalElements());
        response.put("totalPages", responsePage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get complete health vital history by profile ID",
            description = "Retrieves complete health vital history for a specific profile (no pagination)"
    )
    @GetMapping("/profile/{profileId}/history")
    public ResponseEntity<List<HealthVitalResponse>> getHealthVitalHistoryByProfileId(
            @PathVariable Long profileId) {

        log.info("Fetching complete health vital history for profile ID: {}", profileId);

        List<HealthVital> healthVitals = healthVitalService.getHealthVitalHistoryByProfileId(profileId);
        List<HealthVitalResponse> response = healthVitals.stream()
                .map(healthVitalMapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get latest health vital by profile ID",
            description = "Retrieves the most recent health vital for a specific profile"
    )
    @GetMapping("/profile/{profileId}/latest")
    public ResponseEntity<LatestHealthVitalProjection> getLatestHealthVitalByProfileId(
            @PathVariable Long profileId) {

        log.info("Fetching latest health vital for profile ID: {}", profileId);

        LatestHealthVitalProjection latestHealthVital = healthVitalService.getLatestHealthVitalByProfileId(profileId);

        return ResponseEntity.ok(latestHealthVital);
    }

    @Operation(
            summary = "Get latest self health vital",
            description = "Retrieves the most recent health vital for logged-in user"
    )
    @GetMapping("/self/latest")
    public ResponseEntity<LatestHealthVitalProjection> getLatestSelfHealthVital(
            Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String keycloakUserId = jwt.getSubject();
        log.info("Fetching latest health vital for current user: {}", keycloakUserId);

        LatestHealthVitalProjection latestHealthVital = healthVitalService.getLatestHealthVitalByKeycloakUserId(keycloakUserId);

        return ResponseEntity.ok(latestHealthVital);
    }

    @Operation(
            summary = "Get health vital by ID",
            description = "Retrieves a specific health vital by its ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<HealthVitalResponse> getHealthVitalById(@PathVariable Long id) {
        log.info("Fetching health vital with ID: {}", id);

        HealthVital healthVital = healthVitalService.getHealthVitalById(id);
        HealthVitalResponse response = healthVitalMapper.toResponse(healthVital);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Create a new health vital",
            description = "Creates a new health vital record"
    )
    @PostMapping("")
    public ResponseEntity<HealthVitalResponse> createHealthVital(
            @RequestBody HealthVitalRequest request) {

        log.info("Creating new health vital for profile ID: {}", request.getProfileId());

        HealthVital healthVital = healthVitalMapper.toEntity(request);
        HealthVital savedHealthVital = healthVitalService.createHealthVital(healthVital);
        HealthVitalResponse response = healthVitalMapper.toResponse(savedHealthVital);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Update health vital",
            description = "Updates an existing health vital record"
    )
    @PutMapping("/{id}")
    public ResponseEntity<HealthVitalResponse> updateHealthVital(
            @PathVariable Long id,
            @RequestBody HealthVitalRequest request) {

        log.info("Updating health vital with ID: {}", id);

        HealthVital healthVital = healthVitalMapper.toEntity(request);
        HealthVital updatedHealthVital = healthVitalService.updateHealthVital(id, healthVital);
        HealthVitalResponse response = healthVitalMapper.toResponse(updatedHealthVital);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Partially update health vital",
            description = "Partially updates an existing health vital record (only updates provided fields)"
    )
    @PatchMapping("/{id}")
    public ResponseEntity<HealthVitalResponse> partialUpdateHealthVital(
            @PathVariable Long id,
            @RequestBody HealthVitalRequest request) {

        log.info("Partially updating health vital with ID: {}", id);

        HealthVital existingHealthVital = healthVitalService.getHealthVitalById(id);
        healthVitalMapper.partialUpdate(request, existingHealthVital);
        HealthVital updatedHealthVital = healthVitalService.updateHealthVital(id, existingHealthVital);
        HealthVitalResponse response = healthVitalMapper.toResponse(updatedHealthVital);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Delete health vital",
            description = "Deletes a health vital record by ID"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteHealthVital(@PathVariable Long id) {
        log.info("Deleting health vital with ID: {}", id);

        healthVitalService.deleteHealthVital(id);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Health vital deleted successfully");
        response.put("id", id.toString());

        return ResponseEntity.ok(response);
    }
}
