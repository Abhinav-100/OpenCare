package com.ciphertext.opencarebackend.modules.provider.controller;
import com.ciphertext.opencarebackend.modules.provider.dto.filter.MedicalTestFilter;
import com.ciphertext.opencarebackend.modules.provider.dto.request.MedicalTestRequest;
import com.ciphertext.opencarebackend.modules.provider.dto.response.MedicalTestResponse;
import com.ciphertext.opencarebackend.entity.MedicalTest;
import com.ciphertext.opencarebackend.exception.ResourceNotFoundException;
import com.ciphertext.opencarebackend.mapper.MedicalTestMapper;
import com.ciphertext.opencarebackend.modules.provider.service.MedicalTestService;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Slf4j
@RestController
@RequestMapping("/api/medical-tests")
@RequiredArgsConstructor
@Tag(name = "Medical Test Management", description = "API for managing medical tests including listing, retrieval, creation, update and deletion")
public class MedicalTestApiController {
    private final MedicalTestService medicalTestService;
    private final MedicalTestMapper medicalTestMapper;

    @Operation(
            summary = "Get paginated medical tests",
            description = "Retrieves a paginated list of medical tests with optional filters and returns pagination metadata.",
            parameters = {
                    @Parameter(name = "name", description = "Filter by test name"),
                    @Parameter(name = "medicalTestId", description = "Filter by medical test ID"),
                    @Parameter(name = "parentMedicalTestId", description = "Filter by parent medical test ID"),
                    @Parameter(name = "category",description = "Filter by category of medical test"),
                    @Parameter(name = "minPrice", description = "Filter by minimum price"),
                    @Parameter(name = "maxPrice", description = "Filter by maximum price"),
                    @Parameter(name = "page", description = "Page number (0-based)", example = "0"),
                    @Parameter(name = "size", description = "Number of items per page", example = "5")
            }
    )
    @GetMapping("")
    public ResponseEntity<Map<String, Object>> getAllMedicalTestsPage(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer hospitalId,
            @RequestParam(required = false) Integer medicalTestId,
            @RequestParam(required = false) Integer parentMedicalTestId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Pageable pagingSort = PageRequest.of(page, size);
        MedicalTestFilter medicalTestFilter = MedicalTestFilter.builder()
                .name(name)
                .hospitalId(hospitalId)
                .medicalTestId(medicalTestId)
                .parentMedicalTestId(parentMedicalTestId)
                .category(category)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .build();
        Page<MedicalTest> pageMedicalTests = medicalTestService.getPaginatedDataWithFilters(medicalTestFilter, pagingSort);

        Page<MedicalTestResponse> pageMedicalTestResponses = pageMedicalTests.map(medicalTestMapper::toResponse);

        Map<String, Object> response = new HashMap<>();
        response.put("medicalTests", pageMedicalTestResponses.getContent());
        response.put("currentPage", pageMedicalTests.getNumber());
        response.put("totalItems", pageMedicalTests.getTotalElements());
        response.put("totalPages", pageMedicalTests.getTotalPages());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get all medical tests", description = "Retrieves all medical tests without pagination")
    @GetMapping("/all")
    public ResponseEntity<List<MedicalTestResponse>> getAllMedicalTests() {
        log.info("Retrieving all medicalTests");

        List<MedicalTest> medicalTests = medicalTestService.getAllMedicalTests();
        List<MedicalTestResponse> medicalTestResponses = medicalTests.stream()
                .map(medicalTestMapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(medicalTestResponses);
    }

    @Operation(summary = "Get medical test by ID", description = "Retrieves a medical test by its unique ID")
    @GetMapping("/{id}")
    public ResponseEntity<MedicalTestResponse> getMedicalTestById(@PathVariable int id)
            throws ResourceNotFoundException {
        log.info("Retrieving medicalTest with ID: {}", id);

        MedicalTest medicalTest = medicalTestService.getMedicalTestById(id);
        MedicalTestResponse medicalTestResponse = medicalTestMapper.toResponse(medicalTest);

        return ResponseEntity.ok(medicalTestResponse);
    }

    @Operation(summary = "Create a new medical test", description = "Creates a new medical test record with the provided details.")
    @PostMapping
    public ResponseEntity<MedicalTestResponse> createMedicalTest(@RequestBody MedicalTestRequest medicalTestRequest) {
        log.info("Creating medicalTest");

        MedicalTest medicalTest = medicalTestMapper.toEntity(medicalTestRequest);
        MedicalTest newMedicalTest = medicalTestService.createMedicalTest(medicalTest);
        MedicalTestResponse medicalTestResponse = medicalTestMapper.toResponse(newMedicalTest);

        return ResponseEntity.ok(medicalTestResponse);
    }

    @Operation(summary = "Update a medical test", description = "Updates an existing medical test identified by its ID.")
    @PutMapping("/{id}")
    public ResponseEntity<MedicalTestResponse> updateMedicalTest(@RequestBody MedicalTestRequest medicalTestRequest, @PathVariable int id)
            throws ResourceNotFoundException {
        log.info("Updating medicalTest with ID: {}", id);

        MedicalTest medicalTest = medicalTestMapper.toEntity(medicalTestRequest);
        MedicalTest updatedMedicalTest = medicalTestService.updateMedicalTest(medicalTest, id);
        MedicalTestResponse medicalTestResponse = medicalTestMapper.toResponse(updatedMedicalTest);

        return ResponseEntity.ok(medicalTestResponse);
    }

    @Operation(summary = "Delete a medical test", description = "Deletes a medical test identified by its ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedicalTest(@PathVariable int id)
            throws ResourceNotFoundException {
        log.info("Deleting medicalTest with ID: {}", id);

        medicalTestService.deleteMedicalTestById(id);

        return ResponseEntity.noContent().build();
    }
}