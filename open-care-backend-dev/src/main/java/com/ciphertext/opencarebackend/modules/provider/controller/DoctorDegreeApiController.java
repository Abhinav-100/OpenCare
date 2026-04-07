package com.ciphertext.opencarebackend.modules.provider.controller;
import com.ciphertext.opencarebackend.modules.provider.dto.request.DoctorDegreeBatchRequest;
import com.ciphertext.opencarebackend.modules.provider.dto.request.DoctorDegreeRequest;
import com.ciphertext.opencarebackend.modules.provider.dto.response.DoctorDegreeResponse;
import com.ciphertext.opencarebackend.exception.ResourceNotFoundException;
import com.ciphertext.opencarebackend.modules.provider.service.DoctorDegreeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing doctor degree operations.
 * Provides endpoints for CRUD operations on doctor degrees with validation and proper error handling.
 *
 * @author Sadman
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
@Tag(name = "Doctor Degree Management", description = "API for managing doctor degree information including creation, retrieval, updating and deletion of degree records associated with doctors")
/**
 * Flow note: DoctorDegreeApiController belongs to the provider doctor/hospital module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public class DoctorDegreeApiController {

    private final DoctorDegreeService doctorDegreeService;

    @Operation(
            summary = "Get all degrees for a doctor with pagination",
            description = "Retrieves all educational degrees and certifications associated with a specific doctor with pagination support.",
            parameters = {
                    @Parameter(name = "doctorId", description = "ID of the doctor whose degrees to retrieve", required = true),
                    @Parameter(name = "page", description = "Page number (0-indexed)"),
                    @Parameter(name = "size", description = "Number of items per page"),
                    @Parameter(name = "sort", description = "Sort criteria in the format: property(,asc|desc)")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Page of doctor degrees retrieved successfully",
                            content = @Content(schema = @Schema(implementation = Page.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
                    @ApiResponse(responseCode = "404", description = "Doctor not found")
            }
    )
    @GetMapping("/{doctorId}/degrees")
    public ResponseEntity<Page<DoctorDegreeResponse>> getDoctorDegreesByDoctorIdPaginated(
            @PathVariable @Positive(message = "Doctor ID must be positive") Long doctorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir) {

        log.info("Retrieving paginated doctor degrees for doctor ID: {}, page: {}, size: {}, sortBy: {}, sortDir: {}",
                doctorId, page, size, sortBy, sortDir);

        Sort.Direction direction = sortDir.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<DoctorDegreeResponse> degrees = doctorDegreeService.getDoctorDegreesByDoctorId(doctorId, pageable);

        return ResponseEntity.ok(degrees);
    }

    @Operation(
            summary = "Get all degrees for a doctor without pagination",
            description = "Retrieves all educational degrees and certifications associated with a specific doctor without pagination.",
            parameters = {
                    @Parameter(name = "doctorId", description = "ID of the doctor whose degrees to retrieve", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of doctor degrees retrieved successfully",
                            content = @Content(schema = @Schema(implementation = DoctorDegreeResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid doctor ID"),
                    @ApiResponse(responseCode = "404", description = "Doctor not found")
            }
    )
    @GetMapping("/{doctorId}/degrees/all")
    public ResponseEntity<List<DoctorDegreeResponse>> getAllDoctorDegreesByDoctorId(
            @PathVariable @Positive(message = "Doctor ID must be positive") Long doctorId) {

        log.info("Retrieving all doctor degrees for doctor ID: {}", doctorId);

        List<DoctorDegreeResponse> degrees = doctorDegreeService.getDoctorDegreesByDoctorId(doctorId);

        log.info("Successfully retrieved {} degrees for doctor ID: {}", degrees.size(), doctorId);

        return ResponseEntity.ok(degrees);
    }

    @Operation(
            summary = "Get specific degree by ID",
            description = "Retrieves a specific educational degree record by its unique ID for a specific doctor.",
            parameters = {
                    @Parameter(name = "doctorId", description = "ID of the doctor", required = true),
                    @Parameter(name = "id", description = "ID of the degree record to retrieve", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Doctor degree found",
                            content = @Content(schema = @Schema(implementation = DoctorDegreeResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid ID"),
                    @ApiResponse(responseCode = "404", description = "Doctor degree not found")
            }
    )
    @GetMapping("/{doctorId}/degrees/{id}")
    public ResponseEntity<DoctorDegreeResponse> getDoctorDegreeById(
            @PathVariable @Positive(message = "Doctor ID must be positive") Long doctorId,
            @PathVariable @Positive(message = "Degree ID must be positive") Long id) throws ResourceNotFoundException {

        log.info("Retrieving doctor degree with ID: {} for doctor ID: {}", id, doctorId);

        DoctorDegreeResponse degree = doctorDegreeService.getDoctorDegree(doctorId, id);

        log.info("Successfully retrieved degree ID: {} for doctor ID: {}", id, doctorId);

        return ResponseEntity.ok(degree);
    }

    @Operation(
            summary = "Add a new degree to doctor",
            description = "Creates a new educational degree record for a specific doctor including degree details, institution information, and completion dates.",
            parameters = {
                    @Parameter(name = "doctorId", description = "ID of the doctor to add the degree to", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "201", description = "Doctor degree created successfully",
                            content = @Content(schema = @Schema(implementation = DoctorDegreeResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request data"),
                    @ApiResponse(responseCode = "404", description = "Doctor not found"),
                    @ApiResponse(responseCode = "409", description = "Duplicate degree exists")
            }
    )
    @PostMapping("/{doctorId}/degrees")
    public ResponseEntity<DoctorDegreeResponse> createDoctorDegree(
            @PathVariable @Positive(message = "Doctor ID must be positive") Long doctorId,
            @Valid @RequestBody DoctorDegreeRequest doctorDegreeRequest) {

        log.info("Creating doctor degree for doctor ID: {}", doctorId);

        DoctorDegreeResponse createdDegree = doctorDegreeService.createDoctorDegree(doctorId, doctorDegreeRequest);

        log.info("Successfully created degree ID: {} for doctor ID: {}", createdDegree.getId(), doctorId);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdDegree);
    }

    @Operation(
            summary = "Update doctor degree",
            description = "Updates the details of an existing educational degree record for a doctor.",
            parameters = {
                    @Parameter(name = "doctorId", description = "ID of the doctor", required = true),
                    @Parameter(name = "id", description = "ID of the degree record to update", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Doctor degree updated successfully",
                            content = @Content(schema = @Schema(implementation = DoctorDegreeResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request data"),
                    @ApiResponse(responseCode = "404", description = "Doctor degree not found")
            }
    )
    @PutMapping("/{doctorId}/degrees/{id}")
    public ResponseEntity<DoctorDegreeResponse> updateDoctorDegree(
            @PathVariable @Positive(message = "Doctor ID must be positive") Long doctorId,
            @PathVariable @Positive(message = "Degree ID must be positive") Long id,
            @Valid @RequestBody DoctorDegreeRequest doctorDegreeRequest) throws ResourceNotFoundException {

        log.info("Updating doctor degree with ID: {} for doctor ID: {}", id, doctorId);

        DoctorDegreeResponse updatedDegree = doctorDegreeService.updateDoctorDegree(doctorId, id, doctorDegreeRequest);

        log.info("Successfully updated degree ID: {} for doctor ID: {}", id, doctorId);

        return ResponseEntity.ok(updatedDegree);
    }

    @Operation(
            summary = "Delete doctor degree",
            description = "Removes a specific educational degree record from a doctor's profile.",
            parameters = {
                    @Parameter(name = "doctorId", description = "ID of the doctor", required = true),
                    @Parameter(name = "id", description = "ID of the degree record to delete", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = "Doctor degree deleted successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid ID"),
                    @ApiResponse(responseCode = "404", description = "Doctor degree not found")
            }
    )
    @DeleteMapping("/{doctorId}/degrees/{id}")
    public ResponseEntity<Void> deleteDoctorDegree(
            @PathVariable @Positive(message = "Doctor ID must be positive") Long doctorId,
            @PathVariable @Positive(message = "Degree ID must be positive") Long id) throws ResourceNotFoundException {

        log.info("Deleting doctor degree with ID: {} for doctor ID: {}", id, doctorId);

        doctorDegreeService.deleteDoctorDegree(doctorId, id);

        log.info("Successfully deleted degree ID: {} for doctor ID: {}", id, doctorId);

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Batch create/update multiple degrees for doctor",
            description = "Creates and/or updates multiple educational degree records for a specific doctor in a single optimized transaction. " +
                         "If a request contains an 'id' field, it updates that degree; otherwise, it creates a new degree. " +
                         "All operations are performed atomically with minimal database calls for optimal performance.",
            parameters = {
                    @Parameter(name = "doctorId", description = "ID of the doctor to add/update degrees for", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Doctor degrees created/updated successfully",
                            content = @Content(schema = @Schema(implementation = DoctorDegreeResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request data"),
                    @ApiResponse(responseCode = "404", description = "Doctor or degree not found"),
                    @ApiResponse(responseCode = "409", description = "Duplicate degree exists")
            }
    )
    @PostMapping("/{doctorId}/degrees/batch")
    public ResponseEntity<List<DoctorDegreeResponse>> batchUpsertDoctorDegrees(
            @PathVariable @Positive(message = "Doctor ID must be positive") Long doctorId,
            @Valid @RequestBody
            @Size(min = 1, max = 50, message = "Batch request must contain between 1 and 50 items")
            List<@Valid DoctorDegreeBatchRequest> batchRequests) {

        log.info("Batch upserting {} degrees for doctor ID: {}", batchRequests.size(), doctorId);

        List<DoctorDegreeResponse> responses = doctorDegreeService.batchUpsertDoctorDegrees(doctorId, batchRequests);

        log.info("Successfully batch upserted {} degrees for doctor ID: {}", responses.size(), doctorId);

        return ResponseEntity.ok(responses);
    }
}