package com.ciphertext.opencarebackend.modules.provider.controller;

import com.ciphertext.opencarebackend.entity.Hospital;
import com.ciphertext.opencarebackend.exception.ResourceNotFoundException;
import com.ciphertext.opencarebackend.mapper.DoctorMapper;
import com.ciphertext.opencarebackend.mapper.HospitalAmenityMapper;
import com.ciphertext.opencarebackend.mapper.HospitalMapper;
import com.ciphertext.opencarebackend.mapper.HospitalMedicalTestMapper;
import com.ciphertext.opencarebackend.modules.provider.dto.filter.HospitalFilter;
import com.ciphertext.opencarebackend.modules.provider.dto.request.HospitalRequest;
import com.ciphertext.opencarebackend.modules.provider.dto.response.DoctorResponse;
import com.ciphertext.opencarebackend.modules.provider.dto.response.HospitalAmenityResponse;
import com.ciphertext.opencarebackend.modules.provider.dto.response.HospitalMedicalTestResponse;
import com.ciphertext.opencarebackend.modules.provider.dto.response.HospitalResponse;
import com.ciphertext.opencarebackend.modules.provider.service.DoctorWorkplaceService;
import com.ciphertext.opencarebackend.modules.provider.service.HospitalAmenityService;
import com.ciphertext.opencarebackend.modules.provider.service.HospitalMedicalTestService;
import com.ciphertext.opencarebackend.modules.provider.service.HospitalService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@Slf4j
@RestController
@RequestMapping("/api/hospitals")
@RequiredArgsConstructor
@Tag(name = "Hospital Management", description = "API for managing hospital information including creation, retrieval, updating and deletion of hospital records")
/**
 * Flow note: HospitalApiController belongs to the provider doctor/hospital module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public class HospitalApiController {

    private final HospitalService hospitalService;
    private final HospitalMapper hospitalMapper;
    private final DoctorWorkplaceService doctorWorkplaceService;
    private final HospitalAmenityService hospitalAmenityService;
    private final HospitalMedicalTestService hospitalMedicalTestService;
    private final DoctorMapper doctorMapper;
    private final HospitalAmenityMapper hospitalAmenityMapper;
    private final HospitalMedicalTestMapper hospitalMedicalTestMapper;

    @Operation(
        summary = "Get paginated hospitals with filters",
        description = """
            Retrieves a paginated list of hospitals with advanced filtering capabilities.
            Supports filtering by name, bed capacity, location, hospital type, organization type, and services.
            Returns pagination metadata along with the results.
            """,
        parameters = {
            @Parameter(name = "name", description = "Filter by hospital name (English or Bengali)"),
            @Parameter(name = "bnName", description = "Filter by Bengali name"),
            @Parameter(name = "numberOfBed", description = "Filter by number of beds"),
            @Parameter(name = "districtId", description = "Filter by district ID"),
            @Parameter(name = "upazilaId", description = "Filter by upazila ID"),
            @Parameter(name = "unionId", description = "Filter by union ID"),
            @Parameter(name = "hospitalTypes", description = "Filter by hospital type(s)"),
            @Parameter(name = "organizationType", description = "Filter by organization type"),
            @Parameter(name = "isActive", description = "Filter by active status"),
            @Parameter(name = "hasEmergencyService", description = "Filter by emergency service availability"),
            @Parameter(name = "hasAmbulanceService", description = "Filter by ambulance service availability"),
            @Parameter(name = "hasBloodBank", description = "Filter by blood bank availability"),
            @Parameter(name = "page", description = "Page number (0-based)", example = "0"),
            @Parameter(name = "size", description = "Number of items per page", example = "10"),
            @Parameter(name = "sortBy", description = "Field to sort by", example = "name"),
            @Parameter(name = "sortDir", description = "Sort direction (ASC/DESC)", example = "ASC")
        }
    )
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllHospitals(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String bnName,
            @RequestParam(required = false) Integer numberOfBed,
            @RequestParam(required = false) Integer districtId,
            @RequestParam(required = false) Integer upazilaId,
            @RequestParam(required = false) Integer unionId,
            @RequestParam(required = false) List<String> hospitalTypes,
            @RequestParam(required = false) String organizationType,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Boolean hasEmergencyService,
            @RequestParam(required = false) Boolean hasAmbulanceService,
            @RequestParam(required = false) Boolean hasBloodBank,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        HospitalFilter filter = HospitalFilter.builder()
            .name(name)
            .bnName(bnName)
            .numberOfBed(numberOfBed)
            .districtId(districtId)
            .upazilaId(upazilaId)
            .unionId(unionId)
            .hospitalTypes(hospitalTypes)
            .organizationType(organizationType)
            .isActive(isActive)
            .hasEmergencyService(hasEmergencyService)
            .hasAmbulanceService(hasAmbulanceService)
            .hasBloodBank(hasBloodBank)
            .build();

        Page<Hospital> hospitalPage = hospitalService.getPaginatedDataWithFilters(filter, pageable);
        Page<HospitalResponse> responsePage = hospitalPage.map(hospitalMapper::toResponse);

        Map<String, Object> response = new HashMap<>();
        response.put("hospitals", responsePage.getContent());
        response.put("currentPage", responsePage.getNumber());
        response.put("totalItems", responsePage.getTotalElements());
        response.put("totalPages", responsePage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get all hospitals",
            description = "Retrieves a list of all hospitals.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Hospitals retrieved successfully",
                            content = @Content(schema = @Schema(implementation = HospitalResponse.class)))
            }
    )
    @GetMapping("/all")
    public ResponseEntity<List<HospitalResponse>> getAllHospitals() {
        log.info("Retrieving all hospitals");
        List<Hospital> hospitals = hospitalService.getAllHospitals();
        List<HospitalResponse> responses = hospitals.stream()
                .map(hospitalMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @Operation(
        summary = "Get hospital by ID",
        description = """
            Retrieves a hospital by its unique ID with optional related data.
            By default, only basic hospital information is returned. Use query parameters to include additional data.
            """,
        parameters = {
            @Parameter(name = "id", description = "Hospital ID", required = true),
            @Parameter(name = "doctors", description = "Include hospital doctors", example = "true"),
            @Parameter(name = "tests", description = "Include hospital medical tests", example = "true"),
            @Parameter(name = "amenities", description = "Include hospital amenities", example = "true")
        }
    )
    @GetMapping("/{id}")
    public ResponseEntity<HospitalResponse> getHospitalById(
            @PathVariable Integer id,
            @RequestParam(required = false, defaultValue = "false") Boolean doctors,
            @RequestParam(required = false, defaultValue = "false") Boolean tests,
            @RequestParam(required = false, defaultValue = "false") Boolean amenities)
            throws ResourceNotFoundException {
        log.info("Retrieving hospital with ID: {} [doctors={}, tests={}, amenities={}]",
                id, doctors, tests, amenities);

        Hospital hospital = hospitalService.getHospitalById(id);
        HospitalResponse response = hospitalMapper.toResponse(hospital);

        // Conditionally fetch and set doctors
        if (Boolean.TRUE.equals(doctors)) {
            List<DoctorResponse> doctorResponses = doctorWorkplaceService.getDoctorWorkplacesByHospitalId(id)
                .stream()
                .map(workplace -> doctorMapper.toResponse(workplace.getDoctor()))
                .distinct()
                .collect(Collectors.toList());
            response.setDoctors(doctorResponses);
        }

        // Conditionally fetch and set medical tests
        if (Boolean.TRUE.equals(tests)) {
            List<HospitalMedicalTestResponse> testResponses = hospitalMedicalTestService
                .getMedicalTestsByHospitalId(Long.valueOf(id))
                .stream()
                .map(hospitalMedicalTestMapper::toResponse)
                .collect(Collectors.toList());
            response.setTests(testResponses);
        }

        // Conditionally fetch and set amenities
        if (Boolean.TRUE.equals(amenities)) {
            List<HospitalAmenityResponse> amenityResponses = hospitalAmenityService
                .getHospitalAmenitiesByHospitalId(Long.valueOf(id))
                .stream()
                .map(hospitalAmenityMapper::toResponse)
                .collect(Collectors.toList());
            response.setAmenities(amenityResponses);
        }

        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get hospital by registration code",
        description = "Retrieves a hospital by its registration code"
    )
    @GetMapping("/registration/{registrationCode}")
    public ResponseEntity<HospitalResponse> getHospitalByRegistrationCode(@PathVariable String registrationCode)
            throws ResourceNotFoundException {
        log.info("Retrieving hospital with registration code: {}", registrationCode);
        Hospital hospital = hospitalService.getHospitalByRegistrationCode(registrationCode);
        return ResponseEntity.ok(hospitalMapper.toResponse(hospital));
    }

    @Operation(
        summary = "Create a new hospital",
        description = "Creates a new hospital record with the provided details"
    )
    @PostMapping
    public ResponseEntity<HospitalResponse> createHospital(@Valid @RequestBody HospitalRequest request) {
        log.info("Creating hospital: {}", request.getName());
        Hospital hospital = hospitalService.createHospital(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(hospitalMapper.toResponse(hospital));
    }

    @Operation(
        summary = "Update an existing hospital",
        description = "Updates the details of an existing hospital identified by its ID"
    )
    @PutMapping("/{id}")
    public ResponseEntity<HospitalResponse> updateHospital(
            @PathVariable Integer id,
            @Valid @RequestBody HospitalRequest request)
            throws ResourceNotFoundException {
        log.info("Updating hospital with ID: {}", id);
        Hospital hospital = hospitalService.updateHospital(id, request);
        return ResponseEntity.ok(hospitalMapper.toResponse(hospital));
    }

    @Operation(
        summary = "Delete a hospital",
        description = "Deletes a hospital record identified by its ID"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHospital(@PathVariable Integer id)
            throws ResourceNotFoundException {
        log.info("Deleting hospital with ID: {}", id);
        hospitalService.deleteHospital(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Activate a hospital",
        description = "Marks a hospital as active"
    )
    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateHospital(@PathVariable Integer id)
            throws ResourceNotFoundException {
        log.info("Activating hospital with ID: {}", id);
        hospitalService.activateHospital(id);
        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "Deactivate a hospital",
        description = "Marks a hospital as inactive"
    )
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateHospital(@PathVariable Integer id)
            throws ResourceNotFoundException {
        log.info("Deactivating hospital with ID: {}", id);
        hospitalService.deactivateHospital(id);
        return ResponseEntity.ok().build();
    }
}