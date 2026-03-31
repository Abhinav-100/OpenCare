package com.ciphertext.opencarebackend.modules.catalog.controller;

import com.ciphertext.opencarebackend.entity.Division;
import com.ciphertext.opencarebackend.exception.ResourceNotFoundException;
import com.ciphertext.opencarebackend.mapper.DistrictMapper;
import com.ciphertext.opencarebackend.mapper.DivisionMapper;
import com.ciphertext.opencarebackend.mapper.UnionMapper;
import com.ciphertext.opencarebackend.mapper.UpazilaMapper;
import com.ciphertext.opencarebackend.modules.catalog.service.LocationService;
import com.ciphertext.opencarebackend.modules.shared.dto.response.DistrictResponse;
import com.ciphertext.opencarebackend.modules.shared.dto.response.DivisionResponse;
import com.ciphertext.opencarebackend.modules.shared.dto.response.UnionResponse;
import com.ciphertext.opencarebackend.modules.shared.dto.response.UpazilaResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ciphertext.opencarebackend.entity.District;
import com.ciphertext.opencarebackend.entity.Union;
import com.ciphertext.opencarebackend.entity.Upazila;




/**
 * @author Sadman
 */
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Location Management", description = "API for managing geographic location data including divisions, districts, upazilas, and unions for Odisha-focused deployments")
public class LocationApiController {

    private final LocationService service;
    private final DivisionMapper divisionMapper;
    private final DistrictMapper districtMapper;
    private final UpazilaMapper upazilaMapper;
    private final UnionMapper unionMapper;

    @Operation(
            summary = "Get all divisions",
            description = "Retrieves a list of all divisions for Odisha-focused deployments"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved divisions",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DivisionResponse.class)))
    })
    @GetMapping("/divisions")
    public ResponseEntity<List<DivisionResponse>> getAllDivisions() {
        log.info("Retrieving all divisions");

        List<Division> divisions = service.getAllDivisions();
        List<DivisionResponse> divisionResponses = divisions.stream()
                .map(divisionMapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(divisionResponses);
    }

    @Operation(
            summary = "Get division by ID",
            description = "Retrieves a specific division by its unique ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved division",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DivisionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Division not found", content = @Content)
    })
    @GetMapping("/divisions/{id}")
    public ResponseEntity<DivisionResponse> getDivisionById(
            @Parameter(description = "ID of the division to retrieve", required = true, example = "1")
            @PathVariable(value = "id") int divisionId)
            throws ResourceNotFoundException {
        log.info("Retrieving division with ID: {}", divisionId);

        Division division = service.getDivisionById(divisionId);
        DivisionResponse divisionResponse = divisionMapper.toResponse(division);

        return ResponseEntity.ok(divisionResponse);
    }

    @Operation(
            summary = "Get districts by division ID",
            description = "Retrieves all districts that belong to a specific division"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved districts",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DistrictResponse.class))),
            @ApiResponse(responseCode = "404", description = "Division not found", content = @Content)
    })
    @GetMapping("/divisions/{id}/districts")
    public ResponseEntity<List<DistrictResponse>> getAllDistrictsByDivisionId(
            @Parameter(description = "ID of the division to get districts from", required = true, example = "1")
            @PathVariable(value = "id") int divisionId) throws ResourceNotFoundException {
        log.info("Retrieving all districts by division ID: {}", divisionId);
        List<District> districts = service.getAllDistrictsByDivisionId(divisionId);
        List<DistrictResponse> districtResponses = districts.stream()
                .map(districtMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(districtResponses);
    }

    @Operation(
            summary = "Get all districts",
            description = "Retrieves a list of all districts for Odisha-focused deployments"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved districts",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DistrictResponse.class)))
    })
    @GetMapping("/districts")
    public ResponseEntity<List<DistrictResponse>> getAllDistricts() {
        log.info("Retrieving all districts");
        List<District> districts = service.getAllDistricts();
        List<DistrictResponse> districtResponses = districts.stream()
                .map(districtMapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(districtResponses);
    }

    @Operation(
            summary = "Get district by ID",
            description = "Retrieves a specific district by its unique ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved district",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DistrictResponse.class))),
            @ApiResponse(responseCode = "404", description = "District not found", content = @Content)
    })
    @GetMapping("/districts/{id}")
    public ResponseEntity<DistrictResponse> getDistrictById(
            @Parameter(description = "ID of the district to retrieve", required = true, example = "1")
            @PathVariable(value = "id") int districtId)
            throws ResourceNotFoundException {
        log.info("Retrieving district with ID: {}", districtId);

        District district = service.getDistrictById(districtId);
        DistrictResponse districtResponse = districtMapper.toResponse(district);

        return ResponseEntity.ok(districtResponse);
    }

    @Operation(
            summary = "Get upazilas by district ID",
            description = "Retrieves all upazilas that belong to a specific district"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved upazilas",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UpazilaResponse.class))),
            @ApiResponse(responseCode = "404", description = "District not found", content = @Content)
    })
    @GetMapping("/districts/{id}/upazilas")
    public ResponseEntity<List<UpazilaResponse>> getAllUpazilasByDistrictId(
            @Parameter(description = "ID of the district to get upazilas from", required = true, example = "1")
            @PathVariable(value = "id") int districtId) throws ResourceNotFoundException {
        log.info("Retrieving all upazilas by district ID: {}", districtId);
        List<Upazila> upazilas = service.getAllUpazilasByDistrictId(districtId);
        List<UpazilaResponse> upazilaResponses = upazilas.stream()
                .map(upazilaMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(upazilaResponses);
    }

    @Operation(
            summary = "Get all upazilas",
            description = "Retrieves a list of all upazilas for Odisha-focused deployments"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved upazilas",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UpazilaResponse.class)))
    })
    @GetMapping("/upazilas")
    public ResponseEntity<List<UpazilaResponse>> getAllUpazilas() {
        log.info("Retrieving all upazilas");
        List<Upazila> upazilas = service.getAllUpazilas();
        List<UpazilaResponse> upazilaResponses = upazilas.stream()
                .map(upazilaMapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(upazilaResponses);
    }

    @Operation(
            summary = "Get upazila by ID",
            description = "Retrieves a specific upazila by its unique ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved upazila",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UpazilaResponse.class))),
            @ApiResponse(responseCode = "404", description = "Upazila not found", content = @Content)
    })
    @GetMapping("/upazilas/{id}")
    public ResponseEntity<UpazilaResponse> getUpazilaById(
            @Parameter(description = "ID of the upazila to retrieve", required = true, example = "1")
            @PathVariable(value = "id") int upazilaId)
            throws ResourceNotFoundException {
        log.info("Retrieving upazila with ID: {}", upazilaId);

        Upazila upazila = service.getUpazilaById(upazilaId);
        UpazilaResponse upazilaResponse = upazilaMapper.toResponse(upazila);

        return ResponseEntity.ok(upazilaResponse);
    }

    @Operation(
            summary = "Get unions by upazila ID",
            description = "Retrieves all unions that belong to a specific upazila"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved unions",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UnionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Upazila not found", content = @Content)
    })
    @GetMapping("/upazilas/{id}/unions")
    public ResponseEntity<List<UnionResponse>> getAllUnionsByUpazilaId(
            @Parameter(description = "ID of the upazila to get unions from", required = true, example = "1")
            @PathVariable(value = "id") int upazilaId) throws ResourceNotFoundException {
        log.info("Retrieving all unions by upazila ID: {}", upazilaId);
        List<Union> unions = service.getAllUnionsByUpazilaId(upazilaId);
        List<UnionResponse> unionResponses = unions.stream()
                .map(unionMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(unionResponses);
    }
}