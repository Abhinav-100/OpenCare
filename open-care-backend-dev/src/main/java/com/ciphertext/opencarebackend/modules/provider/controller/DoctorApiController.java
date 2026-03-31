package com.ciphertext.opencarebackend.modules.provider.controller;

import com.ciphertext.opencarebackend.entity.Doctor;
import com.ciphertext.opencarebackend.exception.ResourceNotFoundException;
import com.ciphertext.opencarebackend.mapper.DoctorAssociationMapper;
import com.ciphertext.opencarebackend.mapper.DoctorMapper;
import com.ciphertext.opencarebackend.mapper.DoctorWorkplaceMapper;
import com.ciphertext.opencarebackend.modules.provider.dto.filter.DoctorFilter;
import com.ciphertext.opencarebackend.modules.provider.dto.request.DoctorRequest;
import com.ciphertext.opencarebackend.modules.provider.dto.response.DegreeResponse;
import com.ciphertext.opencarebackend.modules.provider.dto.response.DoctorAssociationResponse;
import com.ciphertext.opencarebackend.modules.provider.dto.response.DoctorDegreeResponse;
import com.ciphertext.opencarebackend.modules.provider.dto.response.DoctorResponse;
import com.ciphertext.opencarebackend.modules.provider.dto.response.DoctorWorkplaceResponse;
import com.ciphertext.opencarebackend.modules.provider.dto.response.MedicalSpecialityResponse;
import com.ciphertext.opencarebackend.modules.provider.service.DoctorAssociationService;
import com.ciphertext.opencarebackend.modules.provider.service.DoctorDegreeService;
import com.ciphertext.opencarebackend.modules.provider.service.DoctorService;
import com.ciphertext.opencarebackend.modules.provider.service.DoctorWorkplaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.*;
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
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
@Tag(name = "Doctor Management", description = "API for managing doctor information including creation, retrieval, updating and deletion of doctor records")
public class DoctorApiController {
    private final DoctorService doctorService;
    private final DoctorMapper doctorMapper;
    private final DoctorWorkplaceService doctorWorkplaceService;
    private final DoctorWorkplaceMapper doctorWorkplaceMapper;
    private final DoctorDegreeService doctorDegreeService;
    private final DoctorAssociationService doctorAssociationService;
    private final DoctorAssociationMapper doctorAssociationMapper;

    @Operation(
            summary = "Get paginated doctors with filters",
            description = """
        Retrieves a paginated list of doctors with advanced filtering capabilities.
        Supports filtering by:
        - Name (English or Bengali)
        - BMDC registration number
        - Current workplace status
        - Hospital, work institution, and study institution
        - Medical degree and speciality
        - Professional associations
        - Geographic location (district, upazila, union)
        
        Returns pagination metadata along with the results.
        """,
            parameters = {
                    @Parameter(name = "name", description = "Filter by doctor name"),
                    @Parameter(name = "bmdcNo", description = "Filter by BMDC registration number"),
                    @Parameter(name = "isCurrentWorkplace", description = "Filter by current workplace status"),
                    @Parameter(name = "hospitalId", description = "Filter by hospital ID"),
                    @Parameter(name = "workInstitutionId", description = "Filter by work institution ID"),
                    @Parameter(name = "studyInstitutionId", description = "Filter by study institution ID"),
                    @Parameter(name = "degreeId", description = "Filter by medical degree ID"),
                    @Parameter(name = "specialityId", description = "Filter by medical speciality ID"),
                    @Parameter(name = "associationId", description = "Filter by professional association ID"),
                    @Parameter(name = "districtId", description = "Filter by district ID"),
                    @Parameter(name = "upazilaId", description = "Filter by upazila ID"),
                    @Parameter(name = "unionId", description = "Filter by union ID"),
                    @Parameter(name = "page", description = "Page number (0-based)", example = "0"),
                    @Parameter(name = "size", description = "Number of items per page", example = "5"),
                    @Parameter(name = "sortBy", description = "Field to sort by", example = "name"),
                    @Parameter(name = "sortDir", description = "Sort direction (ASC/DESC)", example = "ASC")
            }
    )
    @GetMapping("")
    public ResponseEntity<Map<String, Object>> getAllDoctorsPage(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String bnName,
            @RequestParam(required = false) String bmdcNo,
            @RequestParam(required = false) Boolean isCurrentWorkplace,
            @RequestParam(required = false) Integer hospitalId,
            @RequestParam(required = false) Integer workInstitutionId,
            @RequestParam(required = false) Integer studyInstitutionId,
            @RequestParam(required = false) Integer degreeId,
            @RequestParam(required = false) Integer specialityId,
            @RequestParam(required = false) Integer associationId,
            @RequestParam(required = false) Integer districtId,
            @RequestParam(required = false) Integer upazilaId,
            @RequestParam(required = false) Integer unionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pagingSort = PageRequest.of(page, size, Sort.by(direction, sortBy));

        DoctorFilter doctorFilter = DoctorFilter.builder()
                .name(name)
                .bnName(bnName)
                .bmdcNo(bmdcNo)
                .isCurrentWorkplace(isCurrentWorkplace)
                .hospitalId(hospitalId)
                .studyInstitutionId(studyInstitutionId)
                .workInstitutionId(workInstitutionId)
                .degreeId(degreeId)
                .specialityId(specialityId)
                .associationId(associationId)
                .districtId(districtId)
                .upazilaId(upazilaId)
                .unionId(unionId)
                .build();
        Page<Doctor> pageDoctors = doctorService.getPaginatedDataWithFilters(doctorFilter, pagingSort);

        Page<DoctorResponse> pageDoctorResponses = pageDoctors.map(doctorMapper::toResponse);

        Map<String, Object> response = new HashMap<>();
        response.put("doctors", pageDoctorResponses.getContent());
        response.put("currentPage", pageDoctors.getNumber());
        response.put("totalItems", pageDoctors.getTotalElements());
        response.put("totalPages", pageDoctors.getTotalPages());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Get all doctors",
            description = "Retrieves a complete list of all doctors without pagination."
    )
    @GetMapping("/all")
    public ResponseEntity<List<DoctorResponse>> getAllDoctors() {
        log.info("Retrieving all doctors");

        List<Doctor> doctors = doctorService.getAllDoctors();
        List<DoctorResponse> doctorResponses = doctors.stream()
                .map(doctorMapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(doctorResponses);
    }

    @Operation(
            summary = "Get doctor by ID",
            description = """
        Retrieves a doctor by their unique ID along with associated information including:
        - Medical degrees and specializations (optional, use ?degrees=true)
        - Workplace information (optional, use ?workplaces=true)
        - Professional associations (optional, use ?associations=true)
        - Aggregated degree abbreviations and specializations
        
        By default, only basic doctor information is returned. Use query parameters to include additional data.
        """,
            parameters = {
                    @Parameter(name = "id", description = "Doctor ID", required = true),
                    @Parameter(name = "degrees", description = "Include doctor degrees", example = "true"),
                    @Parameter(name = "workplaces", description = "Include doctor workplaces", example = "true"),
                    @Parameter(name = "associations", description = "Include doctor associations", example = "true")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponse> getDoctorById(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "false") Boolean degrees,
            @RequestParam(required = false, defaultValue = "false") Boolean workplaces,
            @RequestParam(required = false, defaultValue = "false") Boolean associations)
            throws ResourceNotFoundException {
        log.info("Retrieving doctor with ID: {} [degrees={}, workplaces={}, associations={}]",
                id, degrees, workplaces, associations);

        Doctor doctor = doctorService.getPublicDoctorById(id);
        DoctorResponse doctorResponse = doctorMapper.toResponse(doctor);

        List<DoctorDegreeResponse> doctorDegreeResponses = null;
        List<DoctorWorkplaceResponse> doctorWorkplaceResponses = null;

        // Conditionally fetch and set degrees
        if (Boolean.TRUE.equals(degrees)) {
            doctorDegreeResponses = new ArrayList<>(doctorDegreeService.getDoctorDegreesByDoctorId(doctor.getId()));
            doctorResponse.setDoctorDegrees(doctorDegreeResponses);
        }

        // Conditionally fetch and set workplaces
        if (Boolean.TRUE.equals(workplaces)) {
            doctorWorkplaceResponses = doctorWorkplaceService.getDoctorWorkplacesByDoctorId(doctor.getId())
                    .stream()
                    .map(doctorWorkplaceMapper::toResponse)
                    .collect(Collectors.toList());
            doctorResponse.setDoctorWorkplaces(doctorWorkplaceResponses);
        }

        // Conditionally fetch and set associations
        if (Boolean.TRUE.equals(associations)) {
            List<DoctorAssociationResponse> doctorAssociationResponses = doctorAssociationService
                    .getDoctorAssociationsByDoctorId(doctor.getId())
                    .stream()
                    .map(doctorAssociationMapper::toResponse)
                    .toList();
            doctorResponse.setDoctorAssociations(doctorAssociationResponses);
        }

        // Handle degrees (abbreviations) - only if degrees were fetched
        if (doctorDegreeResponses != null && !doctorDegreeResponses.isEmpty()) {
            Set<String> degreeAbbreviations = doctorDegreeResponses.stream()
                    .map(DoctorDegreeResponse::getDegree)
                    .filter(Objects::nonNull)
                    .map(DegreeResponse::getAbbreviation)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toCollection(LinkedHashSet::new));

            if (!degreeAbbreviations.isEmpty()) {
                doctorResponse.setDegrees(String.join(", ", degreeAbbreviations));
            }
        }

        // Handle specializations (merge from both degrees and workplaces) - only if at least one was fetched
        if (doctorDegreeResponses != null || doctorWorkplaceResponses != null) {
            Set<String> allSpecializations = new LinkedHashSet<>();

            // Add specializations from degrees
            if (doctorDegreeResponses != null) {
                doctorDegreeResponses.stream()
                        .map(DoctorDegreeResponse::getMedicalSpeciality)
                        .filter(Objects::nonNull)
                        .map(MedicalSpecialityResponse::getName)
                        .filter(Objects::nonNull)
                        .forEach(allSpecializations::add);
            }

            // Add specializations from workplaces
            if (doctorWorkplaceResponses != null) {
                doctorWorkplaceResponses.stream()
                        .map(DoctorWorkplaceResponse::getMedicalSpeciality)
                        .filter(Objects::nonNull)
                        .map(MedicalSpecialityResponse::getName)
                        .filter(Objects::nonNull)
                        .forEach(allSpecializations::add);
            }

            if (!allSpecializations.isEmpty()) {
                doctorResponse.setSpecializations(String.join(", ", allSpecializations));
            }
        }

        return ResponseEntity.ok(doctorResponse);
    }

    @Operation(
            summary = "Create a new doctor",
            description = "Creates a new doctor record with the provided details."
    )
    @PostMapping
    public ResponseEntity<DoctorResponse> createDoctor(@Valid @RequestBody DoctorRequest doctorRequest) {
        log.info("Creating doctor: {}", doctorRequest.getName());

        Doctor doctor = doctorService.createDoctor(doctorRequest);
        DoctorResponse doctorResponse = doctorMapper.toResponse(doctor);

        return ResponseEntity.status(HttpStatus.CREATED).body(doctorResponse);
    }

    @Operation(
            summary = "Update an existing doctor",
            description = "Updates the details of an existing doctor identified by their ID."
    )
    @PutMapping("/{id}")
    public ResponseEntity<DoctorResponse> updateDoctor(
            @PathVariable Long id,
            @Valid @RequestBody DoctorRequest doctorRequest)
            throws ResourceNotFoundException {
        log.info("Updating doctor with ID: {}", id);

        Doctor doctor = doctorService.updateDoctor(id, doctorRequest);
        DoctorResponse doctorResponse = doctorMapper.toResponse(doctor);

        return ResponseEntity.ok(doctorResponse);
    }

    @Operation(
            summary = "Delete a doctor",
            description = "Deletes a doctor record identified by their ID."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id)
            throws ResourceNotFoundException {
        log.info("Deleting doctor with ID: {}", id);

        doctorService.deleteDoctorById(id);

        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Verify a doctor",
        description = "Marks a doctor as verified by their ID"
    )
    @PatchMapping("/{id}/verify")
    public ResponseEntity<Void> verifyDoctor(@PathVariable Long id)
            throws ResourceNotFoundException {
        log.info("Verifying doctor with ID: {}", id);
        doctorService.verifyDoctor(id);
        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "Activate a doctor",
        description = "Marks a doctor as active by their ID"
    )
    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateDoctor(@PathVariable Long id)
            throws ResourceNotFoundException {
        log.info("Activating doctor with ID: {}", id);
        doctorService.activateDoctor(id);
        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "Deactivate a doctor",
        description = "Marks a doctor as inactive by their ID"
    )
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateDoctor(@PathVariable Long id)
            throws ResourceNotFoundException {
        log.info("Deactivating doctor with ID: {}", id);
        doctorService.deactivateDoctor(id);
        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "Approve a doctor",
        description = "Approves a pending doctor by verifying and activating the profile"
    )
    @PatchMapping("/{id}/approve")
    public ResponseEntity<Void> approveDoctor(@PathVariable Long id)
            throws ResourceNotFoundException {
        log.info("Approving doctor with ID: {}", id);
        doctorService.approveDoctor(id);
        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "Get doctor by email",
        description = "Retrieves a doctor by their email address"
    )
    @GetMapping("/email/{email}")
    public ResponseEntity<DoctorResponse> getDoctorByEmail(@PathVariable String email)
            throws ResourceNotFoundException {
        log.info("Retrieving doctor with email: {}", email);
        Doctor doctor = doctorService.getPublicDoctorByEmail(email);
        return ResponseEntity.ok(doctorMapper.toResponse(doctor));
    }

    @Operation(
        summary = "Get doctor by username",
        description = "Retrieves a doctor by their username"
    )
    @GetMapping("/username/{username}")
    public ResponseEntity<DoctorResponse> getDoctorByUsername(@PathVariable String username)
            throws ResourceNotFoundException {
        log.info("Retrieving doctor with username: {}", username);
        Doctor doctor = doctorService.getPublicDoctorByUsername(username);
        return ResponseEntity.ok(doctorMapper.toResponse(doctor));
    }

    @Operation(
        summary = "Get doctor by BMDC number",
        description = "Retrieves a doctor by their BMDC registration number"
    )
    @GetMapping("/bmdc/{bmdcNo}")
    public ResponseEntity<DoctorResponse> getDoctorByBmdcNo(@PathVariable String bmdcNo)
            throws ResourceNotFoundException {
        log.info("Retrieving doctor with BMDC number: {}", bmdcNo);
        Doctor doctor = doctorService.getPublicDoctorByBmdcNo(bmdcNo);
        return ResponseEntity.ok(doctorMapper.toResponse(doctor));
    }

    @Operation(
        summary = "Advanced doctor search with comprehensive filtering",
        description = "Advanced search for doctors with multiple filter options including speciality, location, experience, and more"
    )
    @GetMapping("/search/advanced")
    public ResponseEntity<Map<String, Object>> advancedDoctorSearch(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String speciality,
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(required = false) Integer maxExperience,
            @RequestParam(required = false) Boolean isVerified,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) List<Integer> districtIds,
            @RequestParam(required = false) List<Integer> upazilaIds,
            @RequestParam(required = false) List<Integer> unionIds,
            @RequestParam(required = false) List<Integer> degreeIds,
            @RequestParam(required = false) List<Integer> hospitalIds,
            @RequestParam(required = false) List<Integer> institutionIds,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        Pageable pagingSort = PageRequest.of(page, size, 
            Sort.by(Sort.Direction.fromString(sortDirection), sortBy));

        DoctorFilter doctorFilter = DoctorFilter.builder()
                .name(query)
                .bnName(query)
                .speciality(speciality)
                .minExperience(minExperience)
                .maxExperience(maxExperience)
                .isVerified(isVerified)
                .isActive(isActive)
                .districtIds(districtIds)
                .upazilaIds(upazilaIds)
                .unionIds(unionIds)
                .degreeIds(degreeIds)
                .hospitalIds(hospitalIds)
                .institutionIds(institutionIds)
                .build();

        Page<Doctor> pageDoctors = doctorService.getPaginatedDataWithFilters(doctorFilter, pagingSort);
        Page<DoctorResponse> pageDoctorResponses = pageDoctors.map(doctorMapper::toResponse);

        Map<String, Object> response = new HashMap<>();
        response.put("doctors", pageDoctorResponses.getContent());
        response.put("currentPage", pageDoctors.getNumber());
        response.put("totalItems", pageDoctors.getTotalElements());
        response.put("totalPages", pageDoctors.getTotalPages());
        response.put("filters", doctorFilter);
        response.put("sortBy", sortBy);
        response.put("sortDirection", sortDirection);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
        summary = "Quick doctor search for autocomplete",
        description = "Fast search for doctor names and specialities for autocomplete functionality"
    )
    @GetMapping("/search/quick")
    public ResponseEntity<List<Map<String, Object>>> quickDoctorSearch(
            @RequestParam String query,
            @RequestParam(defaultValue = "10") int limit) {

        List<Map<String, Object>> results = doctorService.quickSearch(query, limit);
        return ResponseEntity.ok(results);
    }

    @Operation(
        summary = "Get doctor search suggestions",
        description = "Get searchable terms and suggestions for doctor search"
    )
    @GetMapping("/search/suggestions")
    public ResponseEntity<Map<String, List<String>>> getDoctorSearchSuggestions() {
        Map<String, List<String>> suggestions = doctorService.getSearchSuggestions();
        return ResponseEntity.ok(suggestions);
    }
}