package com.ciphertext.opencarebackend.modules.clinical.controller;

import com.ciphertext.opencarebackend.entity.HealthCondition;
import com.ciphertext.opencarebackend.entity.HealthEncounter;
import com.ciphertext.opencarebackend.entity.HealthMedication;
import com.ciphertext.opencarebackend.modules.clinical.service.HealthRecordsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/health-records")
@RequiredArgsConstructor
@Tag(name = "Health Records", description = "APIs for managing personal health records - encounters, conditions, and medications")
public class HealthRecordsApiController {

    private final HealthRecordsService healthRecordsService;

    // ==================== ENCOUNTERS ====================

    @GetMapping("/encounters/self")
    @Operation(summary = "Get my health encounters", description = "Retrieve all health encounters for the current authenticated user")
    public ResponseEntity<List<HealthEncounter>> getMyEncounters(@AuthenticationPrincipal Jwt jwt) {
        String keycloakUserId = jwt.getSubject();
        log.info("Fetching encounters for user: {}", keycloakUserId);
        List<HealthEncounter> encounters = healthRecordsService.getMyEncounters(keycloakUserId);
        return ResponseEntity.ok(encounters);
    }

    @GetMapping("/encounters/{id}")
    @Operation(summary = "Get encounter by ID", description = "Retrieve a specific health encounter by its ID")
    public ResponseEntity<HealthEncounter> getEncounterById(@PathVariable Long id) {
        HealthEncounter encounter = healthRecordsService.getEncounterById(id);
        return ResponseEntity.ok(encounter);
    }

    @PostMapping("/encounters")
    @Operation(summary = "Create health encounter", description = "Record a new health encounter (doctor visit)")
    public ResponseEntity<HealthEncounter> createEncounter(
            @RequestBody HealthEncounter encounter,
            @AuthenticationPrincipal Jwt jwt) {
        String keycloakUserId = jwt.getSubject();
        log.info("Creating encounter for user: {}", keycloakUserId);
        HealthEncounter created = healthRecordsService.createEncounter(encounter, keycloakUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/encounters/{id}")
    @Operation(summary = "Update health encounter", description = "Update an existing health encounter")
    public ResponseEntity<HealthEncounter> updateEncounter(
            @PathVariable Long id,
            @RequestBody HealthEncounter encounter) {
        HealthEncounter updated = healthRecordsService.updateEncounter(encounter, id);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/encounters/{id}")
    @Operation(summary = "Delete health encounter", description = "Delete a health encounter")
    public ResponseEntity<Void> deleteEncounter(@PathVariable Long id) {
        healthRecordsService.deleteEncounter(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== CONDITIONS ====================

    @GetMapping("/conditions/self")
    @Operation(summary = "Get my health conditions", description = "Retrieve all health conditions (allergies, diagnoses) for the current user")
    public ResponseEntity<List<HealthCondition>> getMyConditions(@AuthenticationPrincipal Jwt jwt) {
        String keycloakUserId = jwt.getSubject();
        log.info("Fetching conditions for user: {}", keycloakUserId);
        List<HealthCondition> conditions = healthRecordsService.getMyConditions(keycloakUserId);
        return ResponseEntity.ok(conditions);
    }

    @GetMapping("/conditions/{id}")
    @Operation(summary = "Get condition by ID", description = "Retrieve a specific health condition by its ID")
    public ResponseEntity<HealthCondition> getConditionById(@PathVariable Long id) {
        HealthCondition condition = healthRecordsService.getConditionById(id);
        return ResponseEntity.ok(condition);
    }

    @PostMapping("/conditions")
    @Operation(summary = "Create health condition", description = "Record a new health condition (allergy, diagnosis, chronic condition)")
    public ResponseEntity<HealthCondition> createCondition(
            @RequestBody HealthCondition condition,
            @AuthenticationPrincipal Jwt jwt) {
        String keycloakUserId = jwt.getSubject();
        log.info("Creating condition for user: {}", keycloakUserId);
        HealthCondition created = healthRecordsService.createCondition(condition, keycloakUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/conditions/{id}")
    @Operation(summary = "Update health condition", description = "Update an existing health condition")
    public ResponseEntity<HealthCondition> updateCondition(
            @PathVariable Long id,
            @RequestBody HealthCondition condition) {
        HealthCondition updated = healthRecordsService.updateCondition(condition, id);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/conditions/{id}")
    @Operation(summary = "Delete health condition", description = "Delete a health condition")
    public ResponseEntity<Void> deleteCondition(@PathVariable Long id) {
        healthRecordsService.deleteCondition(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== MEDICATIONS ====================

    @GetMapping("/medications/self")
    @Operation(summary = "Get my medications", description = "Retrieve all medications for the current user")
    public ResponseEntity<List<HealthMedication>> getMyMedications(@AuthenticationPrincipal Jwt jwt) {
        String keycloakUserId = jwt.getSubject();
        log.info("Fetching medications for user: {}", keycloakUserId);
        List<HealthMedication> medications = healthRecordsService.getMyMedications(keycloakUserId);
        return ResponseEntity.ok(medications);
    }

    @GetMapping("/medications/self/active")
    @Operation(summary = "Get my active medications", description = "Retrieve only active medications for the current user")
    public ResponseEntity<List<HealthMedication>> getMyActiveMedications(@AuthenticationPrincipal Jwt jwt) {
        String keycloakUserId = jwt.getSubject();
        log.info("Fetching active medications for user: {}", keycloakUserId);
        List<HealthMedication> medications = healthRecordsService.getMyActiveMedications(keycloakUserId);
        return ResponseEntity.ok(medications);
    }

    @GetMapping("/medications/{id}")
    @Operation(summary = "Get medication by ID", description = "Retrieve a specific medication by its ID")
    public ResponseEntity<HealthMedication> getMedicationById(@PathVariable Long id) {
        HealthMedication medication = healthRecordsService.getMedicationById(id);
        return ResponseEntity.ok(medication);
    }

    @PostMapping("/medications")
    @Operation(summary = "Create medication", description = "Record a new medication")
    public ResponseEntity<HealthMedication> createMedication(
            @RequestBody HealthMedication medication,
            @AuthenticationPrincipal Jwt jwt) {
        String keycloakUserId = jwt.getSubject();
        log.info("Creating medication for user: {}", keycloakUserId);
        HealthMedication created = healthRecordsService.createMedication(medication, keycloakUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/medications/{id}")
    @Operation(summary = "Update medication", description = "Update an existing medication")
    public ResponseEntity<HealthMedication> updateMedication(
            @PathVariable Long id,
            @RequestBody HealthMedication medication) {
        HealthMedication updated = healthRecordsService.updateMedication(medication, id);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/medications/{id}")
    @Operation(summary = "Delete medication", description = "Delete a medication")
    public ResponseEntity<Void> deleteMedication(@PathVariable Long id) {
        healthRecordsService.deleteMedication(id);
        return ResponseEntity.noContent().build();
    }
}
