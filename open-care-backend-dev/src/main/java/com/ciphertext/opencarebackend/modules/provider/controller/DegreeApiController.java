package com.ciphertext.opencarebackend.modules.provider.controller;
import com.ciphertext.opencarebackend.modules.provider.dto.response.DegreeResponse;
import com.ciphertext.opencarebackend.entity.Degree;
import com.ciphertext.opencarebackend.exception.ResourceNotFoundException;
import com.ciphertext.opencarebackend.mapper.DegreeMapper;
import com.ciphertext.opencarebackend.modules.provider.service.DegreeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/degrees")
@RequiredArgsConstructor
@Tag(name = "Degree Management", description = "API for retrieving degree information")
public class DegreeApiController {
    private final DegreeService degreeService;
    private final DegreeMapper degreeMapper;

    @Operation(
            summary = "Get all degrees",
            description = "Retrieves all degrees without pagination.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Degrees retrieved successfully",
                            content = @Content(schema = @Schema(implementation = DegreeResponse.class)))
            }
    )
    @GetMapping
    public ResponseEntity<List<DegreeResponse>> getAllDegrees() {
        log.info("Retrieving all degrees");

        List<Degree> degrees = degreeService.getAllDegrees();
        List<DegreeResponse> degreeResponses = degrees.stream()
                .map(degreeMapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(degreeResponses);
    }

    @Operation(
            summary = "Get degree by ID",
            description = "Retrieves a degree by its unique ID.",
            parameters = {
                    @Parameter(name = "id", description = "Degree ID", example = "1")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Degree found",
                            content = @Content(schema = @Schema(implementation = DegreeResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Degree not found")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<DegreeResponse> getDegreeById(@PathVariable int id)
            throws ResourceNotFoundException {
        log.info("Retrieving degree with ID: {}", id);

        Degree degree = degreeService.getDegreeById(id);
        DegreeResponse degreeResponse = degreeMapper.toResponse(degree);

        return ResponseEntity.ok(degreeResponse);
    }
}
