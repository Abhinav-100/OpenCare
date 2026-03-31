package com.ciphertext.opencarebackend.modules.shared.controller.external;

import com.ciphertext.opencarebackend.modules.provider.service.elasticsearch.DoctorSearchService;
import com.ciphertext.opencarebackend.modules.provider.service.elasticsearch.HospitalSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/synchronization")
@RequiredArgsConstructor
@Tag(name = "Manual Synchronization", description = "API for manual data synchronization tasks")
public class ManualSynchronizationController {

    private final HospitalSearchService hospitalSearchService;
    private final DoctorSearchService doctorSearchService;

    @Operation(
            summary = "Reindex all hospitals",
            description = "Reindex all hospital data into Elasticsearch"
    )
    @GetMapping("/hospital/reindex")
    public ResponseEntity<String> reindexAllHospitals() {
        log.info("Reindexing all hospitals into Elasticsearch");
        hospitalSearchService.indexAllHospitals();
        return ResponseEntity.ok("Reindexing initiated successfully");
    }

    @Operation(
            summary = "Reindex all doctors",
            description = "Reindex all doctor data into Elasticsearch"
    )
    @GetMapping("/doctor/reindex")
    public ResponseEntity<String> reindexAllDoctors() {
        log.info("Reindexing all doctors into Elasticsearch");
        doctorSearchService.indexAllDoctors();
        return ResponseEntity.ok("Reindexing initiated successfully");
    }
}
