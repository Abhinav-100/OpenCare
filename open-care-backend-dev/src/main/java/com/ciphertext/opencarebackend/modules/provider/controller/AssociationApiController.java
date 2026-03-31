package com.ciphertext.opencarebackend.modules.provider.controller;
import com.ciphertext.opencarebackend.modules.provider.dto.filter.AssociationFilter;
import com.ciphertext.opencarebackend.modules.provider.dto.request.AssociationRequest;
import com.ciphertext.opencarebackend.modules.provider.dto.response.AssociationResponse;
import com.ciphertext.opencarebackend.modules.provider.dto.response.DoctorAssociationResponse;
import com.ciphertext.opencarebackend.entity.Association;
import com.ciphertext.opencarebackend.entity.DoctorAssociation;
import com.ciphertext.opencarebackend.exception.ResourceNotFoundException;
import com.ciphertext.opencarebackend.mapper.AssociationMapper;
import com.ciphertext.opencarebackend.mapper.DoctorAssociationMapper;
import com.ciphertext.opencarebackend.modules.provider.service.AssociationService;
import com.ciphertext.opencarebackend.modules.provider.service.DoctorAssociationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/associations")
@RequiredArgsConstructor
@Tag(name = "Association Management", description = "API for creating, retrieving, updating, and deleting associations")
public class AssociationApiController {

    private final AssociationService associationService;
    private final AssociationMapper associationMapper;
    private final DoctorAssociationService doctorAssociationService;
    private final DoctorAssociationMapper doctorAssociationMapper;

    @Operation(
            summary = "Get paginated associations",
            description = """
            Retrieves a paginated list of associations. Supports sorting by any field.
            Returns pagination metadata along with the results.
            """,
            parameters = {
                    @Parameter(name = "page", description = "Page number (0-based)", example = "0"),
                    @Parameter(name = "size", description = "Number of items per page", example = "5"),
                    @Parameter(name = "sort", description = "Sort field", example = "id"),
                    @Parameter(name = "direction", description = "Sort direction (ASC or DESC)", example = "DESC")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Paginated associations returned")
            }
    )
    @GetMapping("")
    public ResponseEntity<Map<String, Object>> getAllAssociationsPage(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String bnName,
            @RequestParam(required = false) Integer medicalSpecialityId,
            @RequestParam(required = false) String associationType,
            @RequestParam(required = false) String domain,
            @RequestParam(required = false) Integer districtId,
            @RequestParam(required = false) Integer upazilaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "DESC") String direction) {

        Sort.Direction sortDir = Sort.Direction.fromOptionalString(direction.toUpperCase()).orElse(Sort.Direction.DESC);
        Pageable pagingSort = PageRequest.of(page, size, Sort.by(sortDir, sort));
        AssociationFilter associationFilter = AssociationFilter.builder()
                .name(name)
                .bnName(bnName)
                .medicalSpecialityId(medicalSpecialityId)
                .associationType(associationType)
                .domain(domain)
                .districtId(districtId)
                .upazilaId(upazilaId)
                .build();

        Page<Association> pageAssociations = associationService.getPaginatedDataWithFilters(associationFilter, pagingSort);
        Page<AssociationResponse> associationResponses = pageAssociations.map(associationMapper::toResponse);

        Map<String, Object> response = new HashMap<>();
        response.put("associations", associationResponses.getContent());
        response.put("currentPage", pageAssociations.getNumber());
        response.put("totalItems", pageAssociations.getTotalElements());
        response.put("totalPages", pageAssociations.getTotalPages());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Get all associations",
            description = "Retrieves all associations without pagination."
    )
    @GetMapping("/all")
    public ResponseEntity<List<AssociationResponse>> getAllAssociations() {
        log.info("Retrieving all associations");

        List<Association> associations = associationService.getAllAssociations();
        List<AssociationResponse> associationResponses = associations.stream()
                .map(associationMapper::toResponse)
                .toList();

        return ResponseEntity.ok(associationResponses);
    }

    @Operation(
            summary = "Get association by ID",
            description = "Retrieves an association by its unique ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Association found",
                            content = @Content(schema = @Schema(implementation = AssociationResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Association not found")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<AssociationResponse> getAssociationById(@PathVariable Integer id)
            throws ResourceNotFoundException {
        log.info("Retrieving association with ID: {}", id);

        Association association = associationService.getAssociationById(id);
        AssociationResponse response = associationMapper.toResponse(association);

        List<DoctorAssociation> doctorAssociations = doctorAssociationService.getDoctorAssociationsByAssociationId(id);
        List<DoctorAssociationResponse> doctorAssociationInfos = doctorAssociations.stream()
                .map(doctorAssociationMapper::toResponse)
                .toList();
        response.setDoctorAssociations(doctorAssociationInfos);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Create a new association",
            description = "Creates a new association record with the provided details."
    )
    @PostMapping("")
    public ResponseEntity<AssociationResponse> createAssociation(@Valid @RequestBody AssociationRequest request) {
        log.info("Creating association");

        Association created = associationService.createAssociation(request);
        AssociationResponse response = associationMapper.toResponse(created);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Update an existing association",
            description = "Updates the details of an existing association identified by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Association updated"),
                    @ApiResponse(responseCode = "404", description = "Association not found")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<AssociationResponse> updateAssociationById(@Valid @RequestBody AssociationRequest request,
                                                                     @PathVariable Integer id)
            throws ResourceNotFoundException {
        log.info("Updating association with ID: {}", id);

        Association updated = associationService.updateAssociationById(request, id);
        AssociationResponse response = associationMapper.toResponse(updated);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Delete an association",
            description = "Deletes an association identified by its ID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Association deleted"),
                    @ApiResponse(responseCode = "404", description = "Association not found")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAssociationById(@PathVariable Integer id)
            throws ResourceNotFoundException {
        log.info("Deleting association with ID: {}", id);

        associationService.deleteAssociationById(id);
        return ResponseEntity.noContent().build();
    }
}
