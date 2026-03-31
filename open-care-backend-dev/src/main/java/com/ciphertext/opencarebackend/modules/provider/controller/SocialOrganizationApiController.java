package com.ciphertext.opencarebackend.modules.provider.controller;
import com.ciphertext.opencarebackend.modules.provider.dto.filter.SocialOrganizationFilter;
import com.ciphertext.opencarebackend.modules.provider.dto.request.SocialOrganizationRequest;
import com.ciphertext.opencarebackend.modules.provider.dto.response.SocialOrganizationResponse;
import com.ciphertext.opencarebackend.entity.SocialOrganization;
import com.ciphertext.opencarebackend.enums.Country;
import com.ciphertext.opencarebackend.enums.SocialOrganizationType;
import com.ciphertext.opencarebackend.exception.ResourceNotFoundException;
import com.ciphertext.opencarebackend.mapper.SocialOrganizationMapper;
import com.ciphertext.opencarebackend.modules.provider.service.SocialOrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/social-organization")
@RequiredArgsConstructor
@Tag(name = "Social Organization Management", description = "API for managing social organizations, including CRUD operations, filtering, and tag search")
public class SocialOrganizationApiController {
    private final SocialOrganizationService socialOrganizationService;
    private final SocialOrganizationMapper socialOrganizationMapper;

    @Operation(
            summary = "Get paginated and filtered list of social organizations",
            description = "Retrieves a paginated list of social organizations with optional filters such as name, type, phone, etc.",
            parameters = {
                    @Parameter(name = "name", description = "Social organization name (optional)"),
                    @Parameter(name = "bnName", description = "Social organization name in Bangla (optional)"),
                    @Parameter(name = "phone", description = "Phone number (optional)"),
                    @Parameter(name = "email", description = "Email address (optional)"),
                    @Parameter(name = "socialOrganizationType", description = "Type of social organization (optional)"),
                    @Parameter(name = "address", description = "Address (optional)"),
                    @Parameter(name = "originCountry", description = "Origin country (optional)"),
                    @Parameter(name = "page", description = "Page number (default 0)"),
                    @Parameter(name = "size", description = "Page size (default 5)")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Social organizations retrieved successfully",
                            content = @Content(schema = @Schema(implementation = SocialOrganizationResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request parameters")
            }
    )
    @GetMapping("")
    public ResponseEntity<Map<String, Object>> getAllSocialOrganizationsPage(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String bnName,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) SocialOrganizationType socialOrganizationType,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) Country originCountry,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Pageable pagingSort = PageRequest.of(page, size);
        SocialOrganizationFilter socialOrganizationFilter = SocialOrganizationFilter.builder()
                .name(name)
                .bnName(bnName)
                .phone(phone)
                .email(email)
                .socialOrganizationType(socialOrganizationType)
                .address(address)
                .originCountry(originCountry)
                .build();
        Page<SocialOrganization> pageSocialOrganizations = socialOrganizationService.getPaginatedDataWithFilters(socialOrganizationFilter, pagingSort);

        Page<SocialOrganizationResponse> pageSocialOrganizationResponses = pageSocialOrganizations.map(socialOrganizationMapper::toResponse);

        Map<String, Object> response = new HashMap<>();
        response.put("socialOrganizations", pageSocialOrganizationResponses.getContent());
        response.put("currentPage", pageSocialOrganizations.getNumber());
        response.put("totalItems", pageSocialOrganizations.getTotalElements());
        response.put("totalPages", pageSocialOrganizations.getTotalPages());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Get all social organizations",
            description = "Retrieves a list of all social organizations.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Social organizations retrieved successfully",
                            content = @Content(schema = @Schema(implementation = SocialOrganizationResponse.class)))
            }
    )
    @GetMapping("/all")
    public ResponseEntity<List<SocialOrganizationResponse>> getAllSocialOrganizations() {
        log.info("Retrieving all social organization");

        List<SocialOrganization> socialOrganization = socialOrganizationService.getAllSocialOrganizations();
        List<SocialOrganizationResponse> socialOrganizationResponses = socialOrganization.stream()
                .map(socialOrganizationMapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(socialOrganizationResponses);
    }

    @Operation(
            summary = "Get social organization by ID",
            description = "Retrieves the details of a specific social organization by its ID.",
            parameters = {
                    @Parameter(name = "id", description = "ID of the social organization", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Social organization found",
                            content = @Content(schema = @Schema(implementation = SocialOrganizationResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Social organization not found")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<SocialOrganizationResponse> getSocialOrganizationById(@PathVariable int id)
            throws ResourceNotFoundException {
        log.info("Retrieving social organization with ID: {}", id);

        SocialOrganization socialOrganization = socialOrganizationService.getSocialOrganizationById(id);
        SocialOrganizationResponse socialOrganizationResponse = socialOrganizationMapper.toResponse(socialOrganization);

        return ResponseEntity.ok(socialOrganizationResponse);
    }

    @Operation(
            summary = "Create a new social organization",
            description = "Creates a new social organization with the provided details.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Social organization created successfully",
                            content = @Content(schema = @Schema(implementation = SocialOrganizationResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request body")
            }
    )
    @PostMapping("")
    public ResponseEntity<SocialOrganizationResponse> createSocialOrganization(
            @Valid @RequestBody SocialOrganizationRequest request) {
        log.info("Creating new social organization: {}", request.getName());

        SocialOrganization socialOrganization = socialOrganizationService.createSocialOrganization(request);
        SocialOrganizationResponse response = socialOrganizationMapper.toResponse(socialOrganization);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Update social organization by ID",
            description = "Updates an existing social organization with new details.",
            parameters = {
                    @Parameter(name = "id", description = "ID of the social organization to update", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Social organization updated successfully",
                            content = @Content(schema = @Schema(implementation = SocialOrganizationResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Social organization not found"),
                    @ApiResponse(responseCode = "400", description = "Invalid request body")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<SocialOrganizationResponse> updateSocialOrganization(
            @PathVariable int id,
            @Valid @RequestBody SocialOrganizationRequest request) throws ResourceNotFoundException {
        log.info("Updating social organization with ID: {}", id);

        SocialOrganization socialOrganization = socialOrganizationService.updateSocialOrganization(id, request);
        SocialOrganizationResponse response = socialOrganizationMapper.toResponse(socialOrganization);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Delete social organization by ID",
            description = "Deletes a social organization by its ID.",
            parameters = {
                    @Parameter(name = "id", description = "ID of the social organization to delete", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = "Social organization deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Social organization not found")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSocialOrganization(@PathVariable int id) throws ResourceNotFoundException {
        log.info("Deleting social organization with ID: {}", id);

        socialOrganizationService.deleteSocialOrganization(id);

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Search social organizations by tag",
            description = "Retrieves social organizations that match the specified tag.",
            parameters = {
                    @Parameter(name = "tag", description = "Tag to search for", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Social organizations retrieved successfully",
                            content = @Content(schema = @Schema(implementation = SocialOrganizationResponse.class)))
            }
    )
    @GetMapping("/search/tag")
    public ResponseEntity<List<SocialOrganizationResponse>> searchSocialOrganizationsByTag(
            @RequestParam String tag) {
        log.info("Searching social organizations by tag: {}", tag);

        List<SocialOrganization> socialOrganizations = socialOrganizationService.searchByTag(tag);
        List<SocialOrganizationResponse> responses = socialOrganizations.stream()
                .map(socialOrganizationMapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }
}
