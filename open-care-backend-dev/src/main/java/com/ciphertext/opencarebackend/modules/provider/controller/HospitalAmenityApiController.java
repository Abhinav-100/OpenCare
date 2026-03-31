package com.ciphertext.opencarebackend.modules.provider.controller;
import com.ciphertext.opencarebackend.modules.provider.dto.request.HospitalAmenityRequest;
import com.ciphertext.opencarebackend.modules.provider.dto.response.HospitalAmenityResponse;
import com.ciphertext.opencarebackend.entity.HospitalAmenity;
import com.ciphertext.opencarebackend.exception.ResourceNotFoundException;
import com.ciphertext.opencarebackend.mapper.HospitalAmenityMapper;
import com.ciphertext.opencarebackend.modules.provider.service.HospitalAmenityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/hospitals")
@RequiredArgsConstructor
@Tag(name = "Hospital Amenity Management", description = "API for managing hospital amenity information including creation, retrieval, updating and deletion of amenity records associated with hospitals")
public class HospitalAmenityApiController {
    private final HospitalAmenityService hospitalAmenityService;
    private final HospitalAmenityMapper hospitalAmenityMapper;

    @Operation(
            summary = "Get all amenities for a hospital",
            description = "Retrieves all amenities and services available at a specific hospital including rooms, equipment, and facilities.",
            parameters = {
                    @Parameter(name = "hospitalId", description = "ID of the hospital whose amenities to retrieve", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of hospital amenities retrieved successfully",
                            content = @Content(schema = @Schema(implementation = HospitalAmenityResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Hospital not found")
            }
    )
    @GetMapping("/{hospitalId}/amenities")
    public ResponseEntity<List<HospitalAmenityResponse>> getHospitalAmenitiesByHospitalId(
            @PathVariable Long hospitalId) {
        log.info("Retrieving all hospital amenities for hospital ID: {}", hospitalId);

        List<HospitalAmenity> hospitalAmenities = hospitalAmenityService.getHospitalAmenitiesByHospitalId(hospitalId);
        List<HospitalAmenityResponse> hospitalAmenityResponses = hospitalAmenities.stream()
                .map(hospitalAmenityMapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(hospitalAmenityResponses);
    }

    @Operation(
            summary = "Get specific amenity by ID",
            description = "Retrieves a specific hospital amenity record by its unique ID.",
            parameters = {
                    @Parameter(name = "hospitalId", description = "ID of the hospital", required = true),
                    @Parameter(name = "id", description = "ID of the amenity record to retrieve", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Hospital amenity found",
                            content = @Content(schema = @Schema(implementation = HospitalAmenityResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Hospital amenity not found")
            }
    )
    @GetMapping("/{hospitalId}/amenities/{id}")
    public ResponseEntity<HospitalAmenityResponse> getHospitalAmenityById(
            @PathVariable Long hospitalId,
            @PathVariable Long id) throws ResourceNotFoundException {
        log.info("Retrieving hospital amenity with ID: {} for hospital ID: {}", id, hospitalId);

        HospitalAmenity hospitalAmenity = hospitalAmenityService.getHospitalAmenityByIdAndHospitalId(hospitalId, id);
        HospitalAmenityResponse hospitalAmenityResponse = hospitalAmenityMapper.toResponse(hospitalAmenity);

        return ResponseEntity.ok(hospitalAmenityResponse);
    }

    @Operation(
            summary = "Add a new amenity to hospital",
            description = "Creates a new amenity record for a specific hospital including amenity details, pricing, availability, and capacity information.",
            parameters = {
                    @Parameter(name = "hospitalId", description = "ID of the hospital to add the amenity to", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "201", description = "Hospital amenity created successfully",
                            content = @Content(schema = @Schema(implementation = HospitalAmenityResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request data"),
                    @ApiResponse(responseCode = "404", description = "Hospital not found")
            }
    )
    @PostMapping("/{hospitalId}/amenities")
    public ResponseEntity<HospitalAmenityResponse> createHospitalAmenity(
            @PathVariable Long hospitalId,
            @Valid @RequestBody HospitalAmenityRequest hospitalAmenityRequest) {
        log.info("Creating hospital amenity for hospital ID: {}", hospitalId);

        // Set the hospital ID in the request
        hospitalAmenityRequest.setHospitalId(hospitalId);

        HospitalAmenity hospitalAmenity = hospitalAmenityMapper.toEntity(hospitalAmenityRequest);
        HospitalAmenity newHospitalAmenity = hospitalAmenityService.createHospitalAmenity(hospitalAmenity);
        HospitalAmenityResponse hospitalAmenityResponse = hospitalAmenityMapper.toResponse(newHospitalAmenity);

        return ResponseEntity.status(HttpStatus.CREATED).body(hospitalAmenityResponse);
    }

    @Operation(
            summary = "Update hospital amenity",
            description = "Updates the details of an existing amenity record for a hospital including pricing, availability, and other amenity information.",
            parameters = {
                    @Parameter(name = "hospitalId", description = "ID of the hospital", required = true),
                    @Parameter(name = "id", description = "ID of the amenity record to update", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Hospital amenity updated successfully",
                            content = @Content(schema = @Schema(implementation = HospitalAmenityResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Hospital amenity not found"),
                    @ApiResponse(responseCode = "400", description = "Invalid request data")
            }
    )
    @PutMapping("/{hospitalId}/amenities/{id}")
    public ResponseEntity<HospitalAmenityResponse> updateHospitalAmenity(
            @PathVariable Long hospitalId,
            @PathVariable Long id,
            @Valid @RequestBody HospitalAmenityRequest hospitalAmenityRequest) throws ResourceNotFoundException {
        log.info("Updating hospital amenity with ID: {} for hospital ID: {}", id, hospitalId);

        // Set the hospital ID in the request
        hospitalAmenityRequest.setHospitalId(hospitalId);

        hospitalAmenityService.getHospitalAmenityByIdAndHospitalId(hospitalId, id);
        HospitalAmenity hospitalAmenity = hospitalAmenityMapper.toEntity(hospitalAmenityRequest);
        HospitalAmenity updatedHospitalAmenity = hospitalAmenityService.updateHospitalAmenity(hospitalAmenity, id);
        HospitalAmenityResponse hospitalAmenityResponse = hospitalAmenityMapper.toResponse(updatedHospitalAmenity);

        return ResponseEntity.ok(hospitalAmenityResponse);
    }

    @Operation(
            summary = "Delete hospital amenity",
            description = "Removes a specific amenity record from a hospital's available services and facilities.",
            parameters = {
                    @Parameter(name = "hospitalId", description = "ID of the hospital", required = true),
                    @Parameter(name = "id", description = "ID of the amenity record to delete", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = "Hospital amenity deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Hospital amenity not found")
            }
    )
    @DeleteMapping("/{hospitalId}/amenities/{id}")
    public ResponseEntity<Void> deleteHospitalAmenity(
            @PathVariable Long hospitalId,
            @PathVariable Long id) throws ResourceNotFoundException {
        log.info("Deleting hospital amenity with ID: {} for hospital ID: {}", id, hospitalId);

        hospitalAmenityService.getHospitalAmenityByIdAndHospitalId(hospitalId, id);
        hospitalAmenityService.deleteHospitalAmenityById(id);

        return ResponseEntity.noContent().build();
    }
}