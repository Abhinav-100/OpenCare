package com.ciphertext.opencarebackend.modules.provider.controller;
import com.ciphertext.opencarebackend.modules.provider.dto.filter.NurseFilter;
import com.ciphertext.opencarebackend.modules.provider.dto.response.NurseResponse;
import com.ciphertext.opencarebackend.modules.provider.dto.request.NurseRequest;
import com.ciphertext.opencarebackend.entity.Nurse;
import com.ciphertext.opencarebackend.exception.ResourceNotFoundException;
import com.ciphertext.opencarebackend.mapper.NurseMapper;
import com.ciphertext.opencarebackend.modules.provider.service.NurseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/nurses")
@RequiredArgsConstructor
@Tag(name = "Nurse Management", description = "API for managing nurse information including creation, retrieval, updating and deletion of nurse records")
public class NurseApiController {

    private final NurseService nurseService;
    private final NurseMapper nurseMapper;


    @Operation(
            summary = "Get paginated nurses with filters",
            description = """
        Retrieves a paginated list of doctors with advanced filtering capabilities.
        Supports filtering by:
        - Name (English or Bengali)
        - BNMC registration number
        - Geographic location (district, upazila, union)
        Returns pagination metadata along with the results.
        """,
            parameters = {
                    @Parameter(name = "name", description = "Filter by nurse name"),
                    @Parameter(name = "bnmcNo", description = "Filter by BNMC registration number"),
                    @Parameter(name = "districtId", description = "Filter by district ID"),
                    @Parameter(name = "upazilaId", description = "Filter by upazila ID"),
                    @Parameter(name = "unionId", description = "Filter by union ID"),
                    @Parameter(name = "page", description = "Page number (0-based)", example = "0"),
                    @Parameter(name = "size", description = "Number of items per page", example = "5")

            }
    )
    @GetMapping("")
    public ResponseEntity<Map<String, Object>> getAllNursesPage(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String bnmcNo,
            @RequestParam(required = false) Integer districtId,
            @RequestParam(required = false) Integer upazilaId,
            @RequestParam(required = false) Integer unionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size)
{
        Pageable pagingSort = PageRequest.of(page, size);
        NurseFilter nurseFilter = NurseFilter.builder()
                .name(name)
                .bnmcNo(bnmcNo)
                .districtId(districtId)
                .upazilaId(upazilaId)
                .unionId(unionId)
                .build();

        Page<Nurse> pageNurses = nurseService.getPaginatedDataWithFilters(nurseFilter, pagingSort);
        Page<NurseResponse> pageNurseResponses = pageNurses.map(nurseMapper::toResponse);

        Map<String, Object> response = new HashMap<>();
        response.put("nurses", pageNurseResponses.getContent());
        response.put("currentPage", pageNurses.getNumber());
        response.put("totalItems", pageNurses.getTotalElements());
        response.put("totalPages", pageNurses.getTotalPages());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


   @Operation(
           summary = "Get all nurses",
           description = "Retrieves a complete list of all nurses without pagination."
   )
    @GetMapping("/all")
    public ResponseEntity<List<NurseResponse>> getAllNurses() {
        log.info("Retrieving all nurses");

        List<Nurse> nurses = nurseService.getAllNurses();
        List<NurseResponse> nurseResponses = nurses.stream()
                .map(nurseMapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(nurseResponses);
    }


    @Operation(
            summary = "Get nurse by ID",
            description = "Retrieves a nurse by their unique ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<NurseResponse> getNurseById(@PathVariable Long id)
            throws ResourceNotFoundException {
        log.info("Retrieving nurse with ID: {}", id);

        Nurse nurse = nurseService.getNurseById(id);
        NurseResponse nurseResponse = nurseMapper.toResponse(nurse);

        return ResponseEntity.ok(nurseResponse);
    }

    @Operation(
            summary = "Create a new nurse",
            description = "Creates a new nurse record with the provided details."
    )
    @PostMapping
    public ResponseEntity<NurseResponse> createNurse(@RequestBody NurseRequest nurseRequest) {
        log.info("Creating nurse");

        Nurse nurse = nurseMapper.toEntity(nurseRequest);
        Nurse newNurse = nurseService.createNurse(nurse);
        NurseResponse nurseResponse = nurseMapper.toResponse(newNurse);

        return ResponseEntity.ok(nurseResponse);
    }

    @Operation(
            summary = "Update an existing nurse",
            description = "Updates the details of an existing nurse identified by their ID."
    )
    @PutMapping("/{id}")
    public ResponseEntity<NurseResponse> updateNurse(@RequestBody NurseRequest nurseRequest, @PathVariable Long id)
            throws ResourceNotFoundException {
        log.info("Updating nurse with ID: {}", id);

        Nurse nurse = nurseMapper.toEntity(nurseRequest);
        Nurse updatedNurse = nurseService.updateNurse(nurse, id);
        NurseResponse nurseResponse = nurseMapper.toResponse(updatedNurse);

        return ResponseEntity.ok(nurseResponse);
    }

    @Operation(
            summary = "Delete a nurse",
            description = "Deletes a nurse record identified by their ID."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNurse(@PathVariable Long id)
            throws ResourceNotFoundException {
        log.info("Deleting nurse with ID: {}", id);

        nurseService.deleteNurseById(id);
        return ResponseEntity.noContent().build();

    }
}