package com.ciphertext.opencarebackend.modules.blood.controller;
import com.ciphertext.opencarebackend.modules.blood.dto.filter.BloodDonationFilter;
import com.ciphertext.opencarebackend.modules.blood.dto.request.BloodDonationRequest;
import com.ciphertext.opencarebackend.modules.blood.dto.response.BloodDonationResponse;
import com.ciphertext.opencarebackend.entity.BloodDonation;
import com.ciphertext.opencarebackend.enums.BloodComponent;
import com.ciphertext.opencarebackend.enums.BloodGroup;
import com.ciphertext.opencarebackend.mapper.BloodDonationMapper;
import com.ciphertext.opencarebackend.modules.blood.service.BloodDonationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;


@Slf4j
@RestController
@RequestMapping("/api/blood-donations")
@RequiredArgsConstructor
@Tag(name = "Blood Donation Management", description = "API for creating, retrieving, updating, and deleting blood donation records")
public class BloodDonationApiController {

    private final BloodDonationService bloodDonationService;
    private final BloodDonationMapper bloodDonationMapper;

    @Operation(
            summary = "Get paginated blood donations with filters",
            description = "Retrieves a paginated and filtered list of blood donations. Returns pagination metadata along with the results.",
            parameters = {
                    @Parameter(name = "page", description = "Page number (0-based)", example = "0"),
                    @Parameter(name = "size", description = "Number of items per page", example = "5"),
                    @Parameter(name = "donorId", description = "Filter by donor ID"),
                    @Parameter(name = "hospitalId", description = "Filter by hospital ID"),
                    @Parameter(name = "donationDateFrom", description = "Filter donations from this date (YYYY-MM-DD)"),
                    @Parameter(name = "donationDateTo", description = "Filter donations until this date (YYYY-MM-DD)"),
                    @Parameter(name = "bloodGroups", description = "Filter by blood groups (comma separated)"),
                    @Parameter(name = "bloodComponents", description = "Filter by blood components (comma separated)"),
                    @Parameter(name = "minQuantityMl", description = "Minimum quantity in ml"),
                    @Parameter(name = "maxQuantityMl", description = "Maximum quantity in ml"),
                    @Parameter(name = "districtId", description = "Filter by district ID"),
                    @Parameter(name = "upazilaId", description = "Filter by upazila ID")
            }
    )
    @GetMapping("")
    public ResponseEntity<Map<String, Object>> getAllBloodDonationPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) Long donorId,
            @RequestParam(required = false) Integer hospitalId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate donationDateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate donationDateTo,
            @RequestParam(required = false) List<BloodGroup> bloodGroups,
            @RequestParam(required = false) List<BloodComponent> bloodComponents,
            @RequestParam(required = false) Integer minQuantityMl,
            @RequestParam(required = false) Integer maxQuantityMl,
            @RequestParam(required = false) Integer districtId,
            @RequestParam(required = false) Integer upazilaId) {

        BloodDonationFilter filter = BloodDonationFilter.builder()
                .donorId(donorId)
                .hospitalId(hospitalId)
                .donationDateFrom(donationDateFrom)
                .donationDateTo(donationDateTo)
                .bloodGroups(bloodGroups)
                .bloodComponents(bloodComponents)
                .minQuantityMl(minQuantityMl)
                .maxQuantityMl(maxQuantityMl)
                .districtId(districtId)
                .upazilaId(upazilaId)
                .build();

        Pageable pagingSort = PageRequest.of(page, size, Sort.Direction.DESC, "id");
        Page<BloodDonation> pageBloodDonations = bloodDonationService.getPaginatedDataWithFilters(filter, pagingSort);
        Page<BloodDonationResponse> bloodDonationResponses = pageBloodDonations.map(bloodDonationMapper::toResponse);

        Map<String, Object> response = new HashMap<>();
        response.put("donations", bloodDonationResponses.getContent());
        response.put("currentPage", pageBloodDonations.getNumber());
        response.put("totalItems", pageBloodDonations.getTotalElements());
        response.put("totalPages", pageBloodDonations.getTotalPages());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get all blood donations", description = "Retrieves a complete list of all blood donations without pagination.")
    @GetMapping("/all")
    public ResponseEntity<List<BloodDonationResponse>> getAllBloodDonation() {
        List<BloodDonation> bloodDonations = bloodDonationService.getAllBloodDonation();
        List<BloodDonationResponse> bloodDonationResponses = bloodDonations.stream()
                .map(bloodDonationMapper::toResponse)
                .toList();

        return ResponseEntity.ok(bloodDonationResponses);
    }

    @Operation(summary = "Get blood donor by ID", description = "Retrieves a blood donor by their unique ID")
    @GetMapping("/{id}")
    public ResponseEntity<BloodDonationResponse> getBloodDonationById(@PathVariable Long id){
        BloodDonation bloodDonation = bloodDonationService.getBloodDonationById(id);
        BloodDonationResponse bloodDonationResponse = bloodDonationMapper.toResponse(bloodDonation);
        return ResponseEntity.ok(bloodDonationResponse);
    }

    @Operation(summary = "Create a new blood donation", description = "Creates a new blood donation record with the provided details.")
    @PostMapping("")
    public ResponseEntity<BloodDonationResponse> createBloodDonation(@Valid @RequestBody BloodDonationRequest bloodDonationRequest) {
        BloodDonation bloodDonation = bloodDonationMapper.toEntity(bloodDonationRequest);
        bloodDonation = bloodDonationService.createBloodDonation(bloodDonation);
        BloodDonationResponse donationResponse = bloodDonationMapper.toResponse(bloodDonation);
        return new ResponseEntity<>(donationResponse, HttpStatus.CREATED);
    }

    @Operation(summary = "Update a blood donation", description = "Updates an existing blood donation identified by its ID.")
    @PutMapping("/{id}")
    public ResponseEntity<BloodDonationResponse> updateBloodDonationById(@Valid @RequestBody BloodDonationRequest bloodDonationRequest, @PathVariable Long id){
        BloodDonation bloodDonation = bloodDonationMapper.toEntity(bloodDonationRequest);
        bloodDonation = bloodDonationService.updateBloodDonationById(bloodDonation, id);
        BloodDonationResponse donationResponse = bloodDonationMapper.toResponse(bloodDonation);
        return ResponseEntity.ok(donationResponse);
    }

    @Operation(summary = "Delete a blood donation", description = "Deletes a blood donation identified by its ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBloodDonationById(@PathVariable Long id){
        bloodDonationService.deleteBloodDonationById(id);
        return ResponseEntity.noContent().build();
    }
}