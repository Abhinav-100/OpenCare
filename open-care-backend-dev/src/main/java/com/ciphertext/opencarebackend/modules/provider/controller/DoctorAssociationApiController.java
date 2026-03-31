package com.ciphertext.opencarebackend.modules.provider.controller;
import com.ciphertext.opencarebackend.modules.provider.dto.request.DoctorAssociationBatchRequest;
import com.ciphertext.opencarebackend.modules.provider.dto.request.DoctorAssociationRequest;
import com.ciphertext.opencarebackend.modules.provider.dto.response.DoctorAssociationResponse;
import com.ciphertext.opencarebackend.entity.DoctorAssociation;
import com.ciphertext.opencarebackend.exception.ResourceNotFoundException;
import com.ciphertext.opencarebackend.mapper.DoctorAssociationMapper;
import com.ciphertext.opencarebackend.modules.provider.service.DoctorAssociationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
@Tag(name = "Doctor Association Management", description = "API for creating, retrieving, updating, and deleting doctor associations")
public class DoctorAssociationApiController {
    private final DoctorAssociationService doctorAssociationService;
    private final DoctorAssociationMapper doctorAssociationMapper;

    @Operation(
            summary = "Get all associations for a doctor",
            description = "Retrieves all doctor associations for the given doctor ID.",
            parameters = {
                    @Parameter(name = "doctorId", description = "ID of the doctor", required = true, example = "101")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of associations returned",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = DoctorAssociationResponse.class)))
            }
    )
    @GetMapping("/{doctorId}/associations")
    public ResponseEntity<List<DoctorAssociationResponse>> getDoctorAssociationsByDoctorId(@PathVariable Long doctorId) {
        log.info("Retrieving all doctor associations for doctorId: {}", doctorId);

        List<DoctorAssociation> associations = doctorAssociationService.getDoctorAssociationsByDoctorId(doctorId);

        List<DoctorAssociationResponse> responses = associations.stream()
                .map(doctorAssociationMapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @Operation(
            summary = "Get a specific doctor association",
            description = "Retrieves a doctor association by its ID for a given doctor.",
            parameters = {
                    @Parameter(name = "doctorId", description = "ID of the doctor", required = true, example = "101"),
                    @Parameter(name = "id", description = "ID of the association", required = true, example = "1")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Association found",
                            content = @Content(schema = @Schema(implementation = DoctorAssociationResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Association not found")
            }
    )
    @GetMapping("/{doctorId}/associations/{id}")
    public ResponseEntity<DoctorAssociationResponse> getDoctorAssociationById(@PathVariable Long doctorId,
                                                                              @PathVariable Long id)
            throws ResourceNotFoundException {
        log.info("Retrieving doctor association with ID: {} for doctorId: {}", id, doctorId);

        DoctorAssociation doctorAssociation = doctorAssociationService.getDoctorAssociationByIdAndDoctorId(doctorId, id);

        DoctorAssociationResponse response = doctorAssociationMapper.toResponse(doctorAssociation);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Create a doctor association",
            description = "Creates a new doctor association for the specified doctor ID.",
            parameters = {
                    @Parameter(name = "doctorId", description = "ID of the doctor", required = true, example = "101")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Association created",
                            content = @Content(schema = @Schema(implementation = DoctorAssociationResponse.class)))
            }
    )
    @PostMapping("/{doctorId}/associations")
    public ResponseEntity<DoctorAssociationResponse> createDoctorAssociation(@Valid @RequestBody DoctorAssociationRequest request,
                                                                             @PathVariable Long doctorId) {
        log.info("Creating doctor association for doctorId: {}", doctorId);

        // enforce doctorId from path if not present or mismatched
        request.setDoctorId(doctorId);
        DoctorAssociation entity = doctorAssociationMapper.toEntity(request);
        DoctorAssociation created = doctorAssociationService.createDoctorAssociation(entity);
        DoctorAssociationResponse response = doctorAssociationMapper.toResponse(created);
        return ResponseEntity.status(201).body(response);
    }

    @Operation(
            summary = "Update a doctor association",
            description = "Updates an existing doctor association identified by its ID for a given doctor.",
            parameters = {
                    @Parameter(name = "doctorId", description = "ID of the doctor", required = true, example = "101"),
                    @Parameter(name = "id", description = "ID of the association to update", required = true, example = "1")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Association updated"),
                    @ApiResponse(responseCode = "404", description = "Association not found")
            }
    )
    @PutMapping("/{doctorId}/associations/{id}")
    public ResponseEntity<DoctorAssociationResponse> updateDoctorAssociation(@Valid @RequestBody DoctorAssociationRequest request,
                                                                             @PathVariable Long doctorId,
                                                                             @PathVariable Long id)
            throws ResourceNotFoundException {
        log.info("Updating doctor association with ID: {} for doctorId: {}", id, doctorId);

        request.setDoctorId(doctorId);
        DoctorAssociation toUpdate = doctorAssociationMapper.toEntity(request);
        DoctorAssociation updated = doctorAssociationService.updateDoctorAssociationById(doctorId, toUpdate, id);
        DoctorAssociationResponse response = doctorAssociationMapper.toResponse(updated);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Batch upsert doctor associations",
            description = "Creates or updates multiple doctor associations for a given doctor.",
            parameters = {
                    @Parameter(name = "doctorId", description = "ID of the doctor", required = true, example = "101"),
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Associations upserted")
            }
    )
    @PostMapping("/{doctorId}/associations/batch")
    public ResponseEntity<List<DoctorAssociationResponse>> batchUpsertDoctorAssociations(
            @PathVariable @Positive(message = "Doctor ID must be positive") Long doctorId,
            @Valid @RequestBody
            @Size(min = 1, max = 50, message = "Batch request must contain between 1 and 50 items")
            List<@Valid DoctorAssociationBatchRequest> batchRequests) {

        log.info("Batch upserting {} associations for doctor ID: {}", batchRequests.size(), doctorId);

        List<DoctorAssociationResponse> responses = doctorAssociationService.batchUpsertDoctorAssociations(doctorId, batchRequests);

        log.info("Successfully batch upserted {} associations for doctor ID: {}", responses.size(), doctorId);

        return ResponseEntity.ok(responses);
    }

    @Operation(
            summary = "Delete a doctor association",
            description = "Deletes a specific doctor association by its ID.",
            parameters = {
                    @Parameter(name = "doctorId", description = "ID of the doctor", required = true, example = "101"),
                    @Parameter(name = "id", description = "ID of the association to delete", required = true, example = "1")
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = "Association deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Association not found", content = @Content)
            }
    )
    @DeleteMapping("/{doctorId}/associations/{id}")
    public ResponseEntity<Void> deleteDoctorAssociation(
            @PathVariable Long doctorId, @PathVariable Long id) throws ResourceNotFoundException {
        log.info("Deleting doctor association with ID: {} for doctorId: {}", id, doctorId);

        doctorAssociationService.deleteDoctorAssociationById(doctorId, id);
        return ResponseEntity.noContent().build();
    }
}