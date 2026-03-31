package com.ciphertext.opencarebackend.modules.provider.controller;
import com.ciphertext.opencarebackend.modules.provider.dto.request.HospitalMedicalTestRequest;
import com.ciphertext.opencarebackend.modules.provider.dto.response.HospitalMedicalTestResponse;
import com.ciphertext.opencarebackend.entity.HospitalMedicalTest;
import com.ciphertext.opencarebackend.exception.ResourceNotFoundException;
import com.ciphertext.opencarebackend.mapper.HospitalMedicalTestMapper;
import com.ciphertext.opencarebackend.modules.provider.service.HospitalMedicalTestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/hospitals")
@RequiredArgsConstructor
@Tag(
        name = "Hospital Medical Test Management",
        description = "API for managing hospital medical test amenities including creation, retrieval, updating and deletion of medical test records associated with hospitals"
)
public class HospitalMedicalTestApiController {

    private final HospitalMedicalTestService hospitalMedicalTestService;
    private final HospitalMedicalTestMapper hospitalMedicalTestMapper;

    @Operation(
            summary = "Get all medical tests for a hospital",
            description = "Retrieves all medical tests available at a specific hospital.",
            parameters = {
                    @Parameter(name = "hospitalId", description = "ID of the hospital whose medical tests to retrieve", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of hospital medical tests retrieved successfully",
                            content = @Content(schema = @Schema(implementation = HospitalMedicalTestResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Hospital not found")
            }
    )
    @GetMapping("/{hospitalId}/medical-tests")
    public ResponseEntity<List<HospitalMedicalTestResponse>> getMedicalTestsByHospitalId(
            @PathVariable Long hospitalId) {
        log.info("Retrieving all medical tests for hospital ID: {}", hospitalId);

        List<HospitalMedicalTest> medicalTests = hospitalMedicalTestService.getMedicalTestsByHospitalId(hospitalId);
        List<HospitalMedicalTestResponse> responseList = medicalTests.stream()
                .map(hospitalMedicalTestMapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }

    @Operation(
            summary = "Get specific medical test by ID",
            description = "Retrieves a specific hospital medical test record by its unique ID.",
            parameters = {
                    @Parameter(name = "hospitalId", description = "ID of the hospital", required = true),
                    @Parameter(name = "id", description = "ID of the medical test record to retrieve", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Hospital medical test found",
                            content = @Content(schema = @Schema(implementation = HospitalMedicalTestResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Hospital medical test not found")
            }
    )
    @GetMapping("/{hospitalId}/medical-tests/{id}")
    public ResponseEntity<HospitalMedicalTestResponse> getMedicalTestById(
            @PathVariable Long hospitalId,
            @PathVariable Long id) throws ResourceNotFoundException {
        log.info("Retrieving medical test with ID: {} for hospital ID: {}", id, hospitalId);

        HospitalMedicalTest medicalTest = hospitalMedicalTestService.getMedicalTestByIdAndHospitalId(hospitalId, id);
        HospitalMedicalTestResponse response = hospitalMedicalTestMapper.toResponse(medicalTest);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Add a new medical test to hospital",
            description = "Creates a new medical test record for a specific hospital including test details, pricing, and availability.",
            parameters = {
                    @Parameter(name = "hospitalId", description = "ID of the hospital to add the medical test to", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "201", description = "Hospital medical test created successfully",
                            content = @Content(schema = @Schema(implementation = HospitalMedicalTestResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request data"),
                    @ApiResponse(responseCode = "404", description = "Hospital not found")
            }
    )
    @PostMapping("/{hospitalId}/medical-tests")
    public ResponseEntity<HospitalMedicalTestResponse> createMedicalTest(
            @PathVariable Long hospitalId,
            @Valid @RequestBody HospitalMedicalTestRequest request) {
        log.info("Creating medical test for hospital ID: {}", hospitalId);

        request.setHospitalId(hospitalId);

        HospitalMedicalTest medicalTest = hospitalMedicalTestMapper.toEntity(request);
        HospitalMedicalTest newMedicalTest = hospitalMedicalTestService.createMedicalTest(medicalTest);
        HospitalMedicalTestResponse response = hospitalMedicalTestMapper.toResponse(newMedicalTest);

        return ResponseEntity.status(201).body(response);
    }

    @Operation(
            summary = "Update hospital medical test",
            description = "Updates the details of an existing medical test record for a hospital.",
            parameters = {
                    @Parameter(name = "hospitalId", description = "ID of the hospital", required = true),
                    @Parameter(name = "id", description = "ID of the medical test record to update", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Hospital medical test updated successfully",
                            content = @Content(schema = @Schema(implementation = HospitalMedicalTestResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Hospital medical test not found"),
                    @ApiResponse(responseCode = "400", description = "Invalid request data")
            }
    )
    @PutMapping("/{hospitalId}/medical-tests/{id}")
    public ResponseEntity<HospitalMedicalTestResponse> updateMedicalTest(
            @PathVariable Long hospitalId,
            @PathVariable Long id,
            @Valid @RequestBody HospitalMedicalTestRequest request) throws ResourceNotFoundException {
        log.info("Updating medical test with ID: {} for hospital ID: {}", id, hospitalId);

        request.setHospitalId(hospitalId);

        hospitalMedicalTestService.getMedicalTestByIdAndHospitalId(hospitalId, id);
        HospitalMedicalTest medicalTest = hospitalMedicalTestMapper.toEntity(request);
        HospitalMedicalTest updatedMedicalTest = hospitalMedicalTestService.updateMedicalTest(medicalTest, id);
        HospitalMedicalTestResponse response = hospitalMedicalTestMapper.toResponse(updatedMedicalTest);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Delete hospital medical test",
            description = "Removes a specific medical test record from a hospital's services.",
            parameters = {
                    @Parameter(name = "hospitalId", description = "ID of the hospital", required = true),
                    @Parameter(name = "id", description = "ID of the medical test record to delete", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = "Hospital medical test deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Hospital medical test not found")
            }
    )
    @DeleteMapping("/{hospitalId}/medical-tests/{id}")
    public ResponseEntity<Void> deleteMedicalTest(
            @PathVariable Long hospitalId,
            @PathVariable Long id) throws ResourceNotFoundException {
        log.info("Deleting medical test with ID: {} for hospital ID: {}", id, hospitalId);

        hospitalMedicalTestService.getMedicalTestByIdAndHospitalId(hospitalId, id);
        hospitalMedicalTestService.deleteMedicalTestById(id);

        return ResponseEntity.noContent().build();
    }
}