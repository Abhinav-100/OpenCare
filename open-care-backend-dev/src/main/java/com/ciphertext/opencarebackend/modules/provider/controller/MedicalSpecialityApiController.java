package com.ciphertext.opencarebackend.modules.provider.controller;
import com.ciphertext.opencarebackend.modules.provider.dto.filter.MedicalSpecialityFilter;
import com.ciphertext.opencarebackend.modules.provider.dto.request.MedicalSpecialityRequest;
import com.ciphertext.opencarebackend.modules.provider.dto.response.MedicalSpecialityResponse;
import com.ciphertext.opencarebackend.entity.MedicalSpeciality;
import com.ciphertext.opencarebackend.exception.ResourceNotFoundException;
import com.ciphertext.opencarebackend.mapper.MedicalSpecialityMapper;
import com.ciphertext.opencarebackend.modules.provider.service.MedicalSpecialityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/medical-specialities")
@RequiredArgsConstructor
@Tag(name = "Medical Specialities", description = "API for managing medical specialities, including listing, filtering, and CRUD operations")
public class MedicalSpecialityApiController {
    private final MedicalSpecialityService medicalSpecialityService;
    private final MedicalSpecialityMapper medicalSpecialityMapper;

    @Operation(
            summary = "Get paginated and filtered list of medical specialities",
            description = "Retrieves a paginated list of medical specialities with optional filters such as name, description, parent ID, etc.",
            parameters = {
                    @Parameter(name = "name", description = "Medical speciality name (optional)"),
                    @Parameter(name = "bnName", description = "Medical speciality name in Bangla (optional)"),
                    @Parameter(name = "parentId", description = "Parent speciality ID (optional)"),
                    @Parameter(name = "description", description = "Description filter (optional)"),
                    @Parameter(name = "page", description = "Page number (default 0)"),
                    @Parameter(name = "size", description = "Page size (default 5)")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Medical specialities retrieved successfully",
                            content = @Content(schema = @Schema(implementation = MedicalSpecialityResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request parameters")
            }
    )
    @GetMapping("")
    public ResponseEntity<Map<String, Object>> getAllMedicalSpecialitiesPage(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String bnName,
            @RequestParam(required = false) Integer parentId,
            @RequestParam(required = false) String description,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Pageable pagingSort = PageRequest.of(page, size);
        MedicalSpecialityFilter filter = MedicalSpecialityFilter.builder()
                .name(name)
                .bnName(bnName)
                .parentId(parentId)
                .description(description)
                .build();
        Page<MedicalSpeciality> pageSpecialities = medicalSpecialityService.getPaginatedDataWithFilters(filter, pagingSort);

        Page<MedicalSpecialityResponse> pageSpecialityResponses = pageSpecialities.map(medicalSpecialityMapper::toResponse);

        Map<String, Object> response = new HashMap<>();
        response.put("medicalSpecialities", pageSpecialityResponses.getContent());
        response.put("currentPage", pageSpecialities.getNumber());
        response.put("totalItems", pageSpecialities.getTotalElements());
        response.put("totalPages", pageSpecialities.getTotalPages());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Get all medical specialities",
            description = "Returns a list of all available medical specialities."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = MedicalSpecialityResponse.class))))
    })
    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MedicalSpecialityResponse>> getAllMedicalSpecialities() {
        log.info("Retrieving all medical specialities");

        List<MedicalSpeciality> medicalSpecialities = medicalSpecialityService.getAllSpecialities();
        List<MedicalSpecialityResponse> medicalSpecialityResponses = medicalSpecialities.stream()
                .map(medicalSpecialityMapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(medicalSpecialityResponses);
    }

    @Operation(
            summary = "Get medical speciality by id",
            description = "Returns a medical speciality for the given `id`."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MedicalSpecialityResponse.class))),
            @ApiResponse(responseCode = "404", description = "Medical speciality not found",
                    content = @Content)
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MedicalSpecialityResponse> getMedicalSpecialityById(
            @Parameter(description = "Medical speciality id", example = "1")
            @PathVariable int id
    ) throws ResourceNotFoundException {
        log.info("Retrieving medical speciality with ID: {}", id);

        MedicalSpeciality medicalSpeciality = medicalSpecialityService.getSpecialityById(id);
        MedicalSpecialityResponse medicalSpecialityResponse = medicalSpecialityMapper.toResponse(medicalSpeciality);

        return ResponseEntity.ok(medicalSpecialityResponse);
    }

    @Operation(
            summary = "Create a new medical speciality",
            description = "Creates a new medical speciality record with the provided details.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Medical speciality created successfully",
                            content = @Content(schema = @Schema(implementation = MedicalSpecialityResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request body")
            }
    )
    @PostMapping
    public ResponseEntity<MedicalSpecialityResponse> createMedicalSpeciality(@RequestBody MedicalSpecialityRequest medicalSpecialityRequest) {
        log.info("Creating medical speciality");
        MedicalSpeciality medicalSpeciality = medicalSpecialityMapper.toEntity(medicalSpecialityRequest);
        MedicalSpeciality newMedicalSpeciality = medicalSpecialityService.createSpeciality(medicalSpeciality);
        MedicalSpecialityResponse medicalSpecialityResponse = medicalSpecialityMapper.toResponse(newMedicalSpeciality);
        return ResponseEntity.ok(medicalSpecialityResponse);
    }

    @Operation(
            summary = "Update an existing medical speciality",
            description = "Updates the details of an existing medical speciality identified by its ID.",
            parameters = {
                    @Parameter(name = "id", description = "ID of the medical speciality to update", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Medical speciality updated successfully",
                            content = @Content(schema = @Schema(implementation = MedicalSpecialityResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Medical speciality not found")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<MedicalSpecialityResponse> updateMedicalSpeciality(@RequestBody MedicalSpecialityRequest medicalSpecialityRequest, @PathVariable int id)
            throws ResourceNotFoundException {
        log.info("Updating medical speciality with ID: {}", id);

        MedicalSpeciality updatedMedicalSpeciality = medicalSpecialityService.updateSpeciality(medicalSpecialityRequest, id);
        MedicalSpecialityResponse medicalSpecialityResponse = medicalSpecialityMapper.toResponse(updatedMedicalSpeciality);
        return ResponseEntity.ok(medicalSpecialityResponse);
    }

    @Operation(
            summary = "Delete a medical speciality",
            description = "Deletes a medical speciality record identified by its ID.",
            parameters = {
                    @Parameter(name = "id", description = "ID of the medical speciality to delete", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Medical speciality deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Medical speciality not found")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedicalSpeciality(@PathVariable int id) throws ResourceNotFoundException {
        log.info("Deleting medical speciality with ID: {}", id);
        medicalSpecialityService.deleteSpeciality(id);
        return ResponseEntity.ok().build();
    }
}
