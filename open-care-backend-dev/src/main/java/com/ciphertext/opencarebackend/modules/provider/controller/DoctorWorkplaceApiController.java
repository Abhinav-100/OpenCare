package com.ciphertext.opencarebackend.modules.provider.controller;
import com.ciphertext.opencarebackend.modules.provider.dto.request.DoctorWorkplaceBatchRequest;
import com.ciphertext.opencarebackend.modules.provider.dto.request.DoctorWorkplaceRequest;
import com.ciphertext.opencarebackend.modules.provider.dto.response.DoctorWorkplaceResponse;
import com.ciphertext.opencarebackend.entity.DoctorWorkplace;
import com.ciphertext.opencarebackend.exception.ResourceNotFoundException;
import com.ciphertext.opencarebackend.mapper.DoctorWorkplaceMapper;
import com.ciphertext.opencarebackend.modules.provider.service.DoctorWorkplaceService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
@Tag(name = "Doctor Workplace Management", description = "API for managing doctor workplace information including creation, retrieval, updating and deletion of workplace records associated with doctors")
public class DoctorWorkplaceApiController {
    private final DoctorWorkplaceService doctorWorkplaceService;
    private final DoctorWorkplaceMapper doctorWorkplaceMapper;

    @Operation(
            summary = "Get all workplaces for a doctor",
            description = "Retrieves all workplaces associated with a specific doctor.",
            parameters = {
                    @Parameter(name = "doctorId", description = "ID of the doctor whose workplaces to retrieve", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of doctor workplaces retrieved successfully",
                            content = @Content(schema = @Schema(implementation = DoctorWorkplaceResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Doctor not found")
            }
    )
    @GetMapping("/{doctorId}/workplaces")
    public ResponseEntity<List<DoctorWorkplaceResponse>> getDoctorWorkplacesByDoctorId(
            @PathVariable Long doctorId) {
        log.info("Retrieving all doctor workplaces for doctor ID: {}", doctorId);

        List<DoctorWorkplace> doctorWorkplaces = doctorWorkplaceService.getDoctorWorkplacesByDoctorId(doctorId);
        List<DoctorWorkplaceResponse> doctorWorkplaceResponses = doctorWorkplaces.stream()
                .map(doctorWorkplaceMapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(doctorWorkplaceResponses);
    }

    @Operation(
            summary = "Get specific workplace by ID",
            description = "Retrieves a specific workplace record by its unique ID.",
            parameters = {
                    @Parameter(name = "doctorId", description = "ID of the doctor", required = true),
                    @Parameter(name = "id", description = "ID of the workplace record to retrieve", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Doctor workplace found",
                            content = @Content(schema = @Schema(implementation = DoctorWorkplaceResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Doctor workplace not found")
            }
    )
    @GetMapping("/{doctorId}/workplaces/{id}")
    public ResponseEntity<DoctorWorkplaceResponse> getDoctorWorkplaceById(
            @PathVariable Long doctorId,
            @PathVariable Long id) throws ResourceNotFoundException {
        log.info("Retrieving doctor workplace with ID: {} for doctor ID: {}", id, doctorId);

        DoctorWorkplace doctorWorkplace = doctorWorkplaceService.getDoctorWorkplaceByIdAndDoctorId(doctorId, id);
        DoctorWorkplaceResponse doctorWorkplaceResponse = doctorWorkplaceMapper.toResponse(doctorWorkplace);

        return ResponseEntity.ok(doctorWorkplaceResponse);
    }

    @Operation(
            summary = "Add a new workplace to doctor",
            description = "Creates a new workplace record for a specific doctor including workplace details and employment dates.",
            parameters = {
                    @Parameter(name = "doctorId", description = "ID of the doctor to add the workplace to", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "201", description = "Doctor workplace created successfully",
                            content = @Content(schema = @Schema(implementation = DoctorWorkplaceResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request data"),
                    @ApiResponse(responseCode = "404", description = "Doctor not found")
            }
    )
    @PostMapping("/{doctorId}/workplaces")
    public ResponseEntity<DoctorWorkplaceResponse> createDoctorWorkplace(
            @PathVariable Long doctorId,
            @Valid @RequestBody DoctorWorkplaceRequest doctorWorkplaceRequest) {
        log.info("Creating doctor workplace for doctor ID: {}", doctorId);

        doctorWorkplaceRequest.setDoctorId(doctorId);
        DoctorWorkplace doctorWorkplace = doctorWorkplaceMapper.toEntity(doctorWorkplaceRequest);
        DoctorWorkplace newDoctorWorkplace = doctorWorkplaceService.createDoctorWorkplace(doctorWorkplace);
        DoctorWorkplaceResponse doctorWorkplaceResponse = doctorWorkplaceMapper.toResponse(newDoctorWorkplace);

        return ResponseEntity.status(201).body(doctorWorkplaceResponse);
    }

    @Operation(
            summary = "Update doctor workplace",
            description = "Updates the details of an existing workplace record for a doctor.",
            parameters = {
                    @Parameter(name = "doctorId", description = "ID of the doctor", required = true),
                    @Parameter(name = "id", description = "ID of the workplace record to update", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Doctor workplace updated successfully",
                            content = @Content(schema = @Schema(implementation = DoctorWorkplaceResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Doctor workplace not found"),
                    @ApiResponse(responseCode = "400", description = "Invalid request data")
            }
    )
    @PutMapping("/{doctorId}/workplaces/{id}")
    public ResponseEntity<DoctorWorkplaceResponse> updateDoctorWorkplace(
            @PathVariable Long doctorId,
            @PathVariable Long id,
            @Valid @RequestBody DoctorWorkplaceRequest doctorWorkplaceRequest) throws ResourceNotFoundException {
        log.info("Updating doctor workplace with ID: {} for doctor ID: {}", id, doctorId);

        doctorWorkplaceRequest.setDoctorId(doctorId);
        DoctorWorkplace doctorWorkplace = doctorWorkplaceMapper.toEntity(doctorWorkplaceRequest);
        DoctorWorkplace updatedDoctorWorkplace = doctorWorkplaceService.updateDoctorWorkplace(doctorId, doctorWorkplace, id);
        DoctorWorkplaceResponse doctorResponse = doctorWorkplaceMapper.toResponse(updatedDoctorWorkplace);

        return ResponseEntity.ok(doctorResponse);
    }

    @Operation(
            summary = "Delete doctor workplace",
            description = "Removes a specific workplace record from a doctor's profile.",
            parameters = {
                    @Parameter(name = "doctorId", description = "ID of the doctor", required = true),
                    @Parameter(name = "id", description = "ID of the workplace record to delete", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = "Doctor workplace deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Doctor workplace not found")
            }
    )
    @DeleteMapping("/{doctorId}/workplaces/{id}")
    public ResponseEntity<Void> deleteDoctorWorkplace(
            @PathVariable Long doctorId,
            @PathVariable Long id) throws ResourceNotFoundException {
        log.info("Deleting doctor workplace with ID: {} for doctor ID: {}", id, doctorId);

        doctorWorkplaceService.getDoctorWorkplaceByIdAndDoctorId(doctorId, id);
        doctorWorkplaceService.deleteDoctorWorkplaceById(id);

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Batch create/update multiple workplaces for doctor",
            description = "Creates and/or updates multiple workplace records for a specific doctor in a single optimized transaction. " +
                         "If a request contains an 'id' field, it updates that workplace; otherwise, it creates a new workplace. " +
                         "All operations are performed atomically with minimal database calls for optimal performance.",
            parameters = {
                    @Parameter(name = "doctorId", description = "ID of the doctor to add/update workplaces for", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Doctor workplaces created/updated successfully",
                            content = @Content(schema = @Schema(implementation = DoctorWorkplaceResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request data"),
                    @ApiResponse(responseCode = "404", description = "Doctor or workplace not found"),
                    @ApiResponse(responseCode = "409", description = "Duplicate workplace exists")
            }
    )
    @PostMapping("/{doctorId}/workplaces/batch")
    public ResponseEntity<List<DoctorWorkplaceResponse>> batchUpsertDoctorWorkplaces(
            @PathVariable @Positive(message = "Doctor ID must be positive") Long doctorId,
            @Valid @RequestBody
            @Size(min = 1, max = 50, message = "Batch request must contain between 1 and 50 items")
            List<@Valid DoctorWorkplaceBatchRequest> batchRequests) {

        log.info("Batch upserting {} workplaces for doctor ID: {}", batchRequests.size(), doctorId);

        List<DoctorWorkplaceResponse> responses = doctorWorkplaceService.batchUpsertDoctorWorkplaces(doctorId, batchRequests);

        log.info("Successfully batch upserted {} workplaces for doctor ID: {}", responses.size(), doctorId);

        return ResponseEntity.ok(responses);
    }
}