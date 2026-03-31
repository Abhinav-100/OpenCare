package com.ciphertext.opencarebackend.modules.provider.controller;
import com.ciphertext.opencarebackend.modules.provider.dto.filter.AmbulanceFilter;
import com.ciphertext.opencarebackend.modules.provider.dto.request.AmbulanceRequest;
import com.ciphertext.opencarebackend.modules.provider.dto.response.AmbulanceResponse;
import com.ciphertext.opencarebackend.entity.Ambulance;
import com.ciphertext.opencarebackend.mapper.AmbulanceMapper;
import com.ciphertext.opencarebackend.modules.provider.service.AmbulanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Slf4j
@RestController
@RequestMapping("/api/ambulances")
@RequiredArgsConstructor
@Tag(name = "Ambulance Management", description = "API for managing ambulances including creation, retrieval, updating and deletion of ambulance records")
public class AmbulanceApiController {

    private final AmbulanceService ambulanceService;
    private final AmbulanceMapper ambulanceMapper;

    @Operation(
            summary = "Get paginated ambulances",
            description = "Retrieves a paginated list of ambulances. Returns pagination metadata along with the results.",
            parameters = {
                    @Parameter(name = "page", description = "Page number (0-based)", example = "0"),
                    @Parameter(name = "size", description = "Number of items per page", example = "5")
            }
    )
    @GetMapping("")
    public ResponseEntity<Map<String, Object>> getAllAmbulancesPage(
            @RequestParam(required = false) String vehicleNumber,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String driverName,
            @RequestParam(required = false) String driverPhone,
            @RequestParam(required = false) Boolean isAvailable,
            @RequestParam(required = false) Boolean isAffiliated,
            @RequestParam(required = false) Integer hospitalId,
            @RequestParam(required = false) Integer districtId,
            @RequestParam(required = false) Integer upazilaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "DESC") String direction) {

        Sort.Direction sortDir = Sort.Direction.fromOptionalString(direction.toUpperCase()).orElse(Sort.Direction.DESC);
        Pageable pagingSort = PageRequest.of(page, size, Sort.by(sortDir, sort));
        AmbulanceFilter ambulanceFilter = AmbulanceFilter.builder()
                .vehicleNumber(vehicleNumber)
                .type(type)
                .driverName(driverName)
                .driverPhone(driverPhone)
                .isAvailable(isAvailable)
                .isAffiliated(isAffiliated)
                .hospitalId(hospitalId)
                .districtId(districtId)
                .upazilaId(upazilaId)
                .build();

        Page<Ambulance> pageAmbulances = ambulanceService.getPaginatedDataWithFilters(ambulanceFilter, pagingSort);
        Page<AmbulanceResponse> ambulanceResponses = pageAmbulances.map(ambulanceMapper::toResponse);

        Map<String, Object> response = new HashMap<>();
        response.put("ambulances", ambulanceResponses.getContent());
        response.put("currentPage", pageAmbulances.getNumber());
        response.put("totalItems", pageAmbulances.getTotalElements());
        response.put("totalPages", pageAmbulances.getTotalPages());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get all ambulances", description = "Retrieves a complete list of all ambulances without pagination.")
    @GetMapping("/all")
    public ResponseEntity<List<AmbulanceResponse>> getAllAmbulances() {
        List<Ambulance> ambulances = ambulanceService.getAllAmbulance();
        List<AmbulanceResponse> amResponses = ambulances.stream()
                .map(ambulanceMapper::toResponse)
                .toList();
        return ResponseEntity.ok(amResponses);
    }

    @Operation(summary = "Get ambulance by ID", description = "Retrieves an ambulance by its unique ID")
    @GetMapping("/{id}")
    public ResponseEntity<AmbulanceResponse> getAmbulanceById(@PathVariable Integer id) {
        Ambulance amb = ambulanceService.getAmbulanceById(id);
        AmbulanceResponse ambResponse = ambulanceMapper.toResponse(amb);
        return ResponseEntity.ok(ambResponse);
    }

    @Operation(summary = "Create a new ambulance", description = "Creates a new ambulance record with the provided details.")
    @PostMapping
    public ResponseEntity<AmbulanceResponse> createAmbulance(@Valid @RequestBody AmbulanceRequest ambRequest) {
        Ambulance amb = ambulanceMapper.toEntity(ambRequest);
        amb = ambulanceService.createAmbulance(amb);
        AmbulanceResponse ambResponse = ambulanceMapper.toResponse(amb);
        return new ResponseEntity<>(ambResponse, HttpStatus.CREATED);
    }

    @Operation(summary = "Update an ambulance", description = "Updates an existing ambulance identified by its ID.")
    @PutMapping("/{id}")
    public ResponseEntity<AmbulanceResponse> updateAmbulanceById(@Valid @RequestBody AmbulanceRequest ambRequest, @PathVariable Integer id) {
        Ambulance amb = ambulanceMapper.toEntity(ambRequest);
        amb = ambulanceService.updateAmbulanceById(amb, id);
        AmbulanceResponse ambResponse = ambulanceMapper.toResponse(amb);
        return ResponseEntity.ok(ambResponse);
    }

    @Operation(summary = "Delete an ambulance", description = "Deletes an ambulance identified by its ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAmbulanceById(@PathVariable Integer id) {
        ambulanceService.deleteAmbulanceById(id);
        return ResponseEntity.noContent().build();
    }
}
