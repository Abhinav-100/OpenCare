package com.ciphertext.opencarebackend.modules.provider.controller;
import com.ciphertext.opencarebackend.modules.provider.dto.filter.InstitutionFilter;
import com.ciphertext.opencarebackend.modules.provider.dto.request.InstitutionRequest;
import com.ciphertext.opencarebackend.modules.provider.dto.response.InstitutionResponse;
import com.ciphertext.opencarebackend.entity.Institution;
import com.ciphertext.opencarebackend.entity.Tag;
import com.ciphertext.opencarebackend.exception.ResourceNotFoundException;
import com.ciphertext.opencarebackend.mapper.InstitutionMapper;
import com.ciphertext.opencarebackend.modules.provider.service.InstitutionService;
import com.ciphertext.opencarebackend.modules.catalog.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/institutions")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Institution Management", description = "API for managing institutions, including listing, filtering, and retrieving institution details")
public class InstitutionApiController {
    private final InstitutionService institutionService;
    private final InstitutionMapper institutionMapper;
    private final TagService tagService;

    @Operation(
            summary = "Get paginated and filtered list of institutions",
            description = "Retrieves a paginated list of institutions with optional filters such as name, district, type, etc.",
            parameters = {
                    @Parameter(name = "name", description = "Institution name (optional)"),
                    @Parameter(name = "bnName", description = "Institution name in Bangla (optional)"),
                    @Parameter(name = "enroll", description = "Enrollment number (optional)"),
                    @Parameter(name = "districtIds", description = "List of district IDs (optional)"),
                    @Parameter(name = "country", description = "Country name (optional)"),
                    @Parameter(name = "institutionTypes", description = "List of institution types (optional)"),
                    @Parameter(name = "organizationType", description = "Organization type (optional)"),
                    @Parameter(name = "page", description = "Page number (default 0)"),
                    @Parameter(name = "size", description = "Page size (default 5)")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Institutions retrieved successfully",
                            content = @Content(schema = @Schema(implementation = InstitutionResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request parameters")
            }
    )
    @GetMapping("")
    public ResponseEntity<Map<String, Object>> getAllInstitutionsPage(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String bnName,
            @RequestParam(required = false) Integer enroll,
            @RequestParam(required = false) List<Integer> districtIds,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) List<String> institutionTypes,
            @RequestParam(required = false) String organizationType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Pageable pagingSort = PageRequest.of(page, size);
        InstitutionFilter institutionFilter = InstitutionFilter.builder()
                .name(name)
                .bnName(bnName)
                .districtIds(districtIds)
                .enroll(enroll)
                .organizationType(organizationType)
                .institutionTypes(institutionTypes)
                .country(country)
                .build();
        Page<Institution> pageInstitutions = institutionService.getPaginatedDataWithFilters(institutionFilter, pagingSort);

        Page<InstitutionResponse> pageInstitutionResponses = pageInstitutions.map(institutionMapper::toResponse);

        Map<String, Object> response = new HashMap<>();
        response.put("institutions", pageInstitutionResponses.getContent());
        response.put("currentPage", pageInstitutions.getNumber());
        response.put("totalItems", pageInstitutions.getTotalElements());
        response.put("totalPages", pageInstitutions.getTotalPages());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Get all institutions",
            description = "Retrieves a list of all institutions.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Institutions retrieved successfully",
                            content = @Content(schema = @Schema(implementation = InstitutionResponse.class)))
            }
    )
    @GetMapping("/all")
    public ResponseEntity<List<InstitutionResponse>> getAllInstitutions() {
        log.info("Retrieving all institutions");

        List<Institution> institutions = institutionService.getAllInstitutions();
        List<InstitutionResponse> institutionResponses = institutions.stream()
                .map(institutionMapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(institutionResponses);
    }

    @Operation(
            summary = "Get institution by ID",
            description = "Retrieves the details of a specific institution by its ID.",
            parameters = {
                    @Parameter(name = "id", description = "ID of the institution", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Institution found",
                            content = @Content(schema = @Schema(implementation = InstitutionResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Institution not found")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<InstitutionResponse> getInstitutionById(@PathVariable int id)
            throws ResourceNotFoundException {
        log.info("Retrieving institution with ID: {}", id);

        Institution institution = institutionService.getInstitutionById(id);
        InstitutionResponse institutionResponse = institutionMapper.toResponse(institution);

        return ResponseEntity.ok(institutionResponse);
    }

    @Operation(
            summary = "Create a new institution",
            description = "Creates a new institution record with the provided details."
    )
    @PostMapping
    public ResponseEntity<InstitutionResponse> createInstitution(@RequestBody InstitutionRequest institutionRequest) {
        log.info("Creating institution");
        Institution institution = institutionMapper.toEntity(institutionRequest);
        Institution newInstitution = institutionService.createInstitution(institution);
        InstitutionResponse institutionResponse = institutionMapper.toResponse(newInstitution);
        return ResponseEntity.ok(institutionResponse);
    }

    @Operation(
            summary = "Update an existing institution",
            description = "Updates the details of an existing institution identified by its ID.",
            parameters = {
                    @Parameter(name = "id", description = "ID of the institution to update", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Institution updated successfully",
                            content = @Content(schema = @Schema(implementation = InstitutionResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Institution not found")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<InstitutionResponse> updateInstitution(@RequestBody InstitutionRequest institutionRequest, @PathVariable int id)
            throws ResourceNotFoundException {
        log.info("Updating institution with ID: {}", id);

        // Handle tag updates if tagIds are provided
        if (institutionRequest.getTagIds() != null && !institutionRequest.getTagIds().isEmpty()) {
            Set<Tag> tags = new HashSet<>();
            for (Integer tagId : institutionRequest.getTagIds()) {
                tagService.getTagById(tagId).ifPresent(tags::add);
            }
            log.info("Setting {} tags for institution update", tags.size());
            Institution updatedInstitution = institutionService.updateInstitutionWithTags(institutionRequest, id, tags);
            InstitutionResponse institutionResponse = institutionMapper.toResponse(updatedInstitution);
            return ResponseEntity.ok(institutionResponse);
        } else {
            Institution updatedInstitution = institutionService.updateInstitution(institutionRequest, id);
            InstitutionResponse institutionResponse = institutionMapper.toResponse(updatedInstitution);
            return ResponseEntity.ok(institutionResponse);
        }
    }

    @Operation(
            summary = "Delete an institution",
            description = "Deletes an institution record identified by its ID.",
            parameters = {
                    @Parameter(name = "id", description = "ID of the institution to delete", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Institution deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Institution not found")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInstitution(@PathVariable int id) throws ResourceNotFoundException {
        log.info("Deleting institution with ID: {}", id);
        institutionService.deleteInstitutionById(id);
        return ResponseEntity.ok().build();
    }
}