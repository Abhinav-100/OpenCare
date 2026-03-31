package com.ciphertext.opencarebackend.modules.blood.controller;
import com.ciphertext.opencarebackend.modules.blood.dto.filter.BloodDonorFilter;
import com.ciphertext.opencarebackend.modules.blood.dto.response.BloodDonorResponse;
import com.ciphertext.opencarebackend.entity.Profile;
import com.ciphertext.opencarebackend.enums.BloodGroup;
import com.ciphertext.opencarebackend.enums.Gender;
import com.ciphertext.opencarebackend.mapper.BloodDonorMapper;
import com.ciphertext.opencarebackend.modules.blood.service.BloodDonorService;
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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Slf4j
@RestController
@RequestMapping("/api/blood-donors")
@RequiredArgsConstructor
@Tag(name = "Blood Donor Management", description = "API for retrieving blood donor information from user profiles")
public class BloodDonorApiController {

    private final BloodDonorService bloodDonorService;
    private final BloodDonorMapper bloodDonorMapper;

    @Operation(
            summary = "Get paginated blood donors with filters",
            description = "Retrieves a paginated and filtered list of blood donors from user profiles. Only returns users who are registered as blood donors.",
            parameters = {
                    @Parameter(name = "page", description = "Page number (0-based)", example = "0"),
                    @Parameter(name = "size", description = "Number of items per page", example = "10"),
                    @Parameter(name = "name", description = "Filter by donor name"),
                    @Parameter(name = "phone", description = "Filter by phone number"),
                    @Parameter(name = "genders", description = "Filter by genders (comma separated)"),
                    @Parameter(name = "bloodGroups", description = "Filter by blood groups (comma separated)"),
                    @Parameter(name = "districtId", description = "Filter by district ID"),
                    @Parameter(name = "upazilaId", description = "Filter by upazila ID"),
                    @Parameter(name = "unionId", description = "Filter by union ID"),
                    @Parameter(name = "minDonationCount", description = "Minimum donation count"),
                    @Parameter(name = "maxDonationCount", description = "Maximum donation count"),
                    @Parameter(name = "lastDonationDateFrom", description = "Filter by last donation date from (yyyy-MM-dd)"),
                    @Parameter(name = "lastDonationDateTo", description = "Filter by last donation date to (yyyy-MM-dd)"),
                    @Parameter(name = "isActive", description = "Filter by active status"),
                    @Parameter(name = "searchText", description = "Search in name, phone, and address"),
                    @Parameter(name = "sortBy", description = "Sort field (name, bloodDonationCount, lastBloodDonationDate)", example = "bloodDonationCount"),
                    @Parameter(name = "sortDir", description = "Sort direction (asc, desc)", example = "desc")
            }
    )
    @GetMapping("")
    public ResponseEntity<Map<String, Object>> getAllBloodDonorsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) List<Gender> genders,
            @RequestParam(required = false) List<BloodGroup> bloodGroups,
            @RequestParam(required = false) Integer districtId,
            @RequestParam(required = false) Integer upazilaId,
            @RequestParam(required = false) Integer unionId,
            @RequestParam(required = false) Integer minDonationCount,
            @RequestParam(required = false) Integer maxDonationCount,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date lastDonationDateFrom,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date lastDonationDateTo,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) String searchText,
            @RequestParam(defaultValue = "bloodDonationCount") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        BloodDonorFilter filter = BloodDonorFilter.builder()
                .name(name)
                .phone(phone)
                .genders(genders)
                .bloodGroups(bloodGroups)
                .districtId(districtId)
                .upazilaId(upazilaId)
                .unionId(unionId)
                .minDonationCount(minDonationCount)
                .maxDonationCount(maxDonationCount)
                .lastDonationDateFrom(lastDonationDateFrom)
                .lastDonationDateTo(lastDonationDateTo)
                .isActive(isActive)
                .searchText(searchText)
                .build();

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, direction, sortBy);

        Page<Profile> donorProfiles = bloodDonorService.getPaginatedBloodDonorsWithFilters(filter, pageable);
        Page<BloodDonorResponse> donorResponses = donorProfiles.map(bloodDonorMapper::toResponse);

        Map<String, Object> response = new HashMap<>();
        response.put("donors", donorResponses.getContent());
        response.put("currentPage", donorProfiles.getNumber());
        response.put("totalItems", donorProfiles.getTotalElements());
        response.put("totalPages", donorProfiles.getTotalPages());
        response.put("hasNext", donorProfiles.hasNext());
        response.put("hasPrevious", donorProfiles.hasPrevious());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Get blood donor statistics",
            description = "Returns statistical information about blood donors including counts by blood group, location, etc."
    )
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getBloodDonorStatistics() {
        // This could be extended to provide donor statistics
        Map<String, Object> stats = new HashMap<>();
        stats.put("message", "Statistics endpoint - can be implemented as needed");
        return ResponseEntity.ok(stats);
    }
}