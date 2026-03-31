package com.ciphertext.opencarebackend.modules.blood.controller;
import com.ciphertext.opencarebackend.modules.blood.dto.filter.BloodRequisitionFilter;
import com.ciphertext.opencarebackend.modules.blood.dto.request.BloodRequisitionRequest;
import com.ciphertext.opencarebackend.modules.blood.dto.response.BloodRequisitionResponse;
import com.ciphertext.opencarebackend.entity.BloodRequisition;
import com.ciphertext.opencarebackend.enums.BloodComponent;
import com.ciphertext.opencarebackend.enums.BloodGroup;
import com.ciphertext.opencarebackend.enums.Gender;
import com.ciphertext.opencarebackend.enums.RequisitionStatus;
import com.ciphertext.opencarebackend.mapper.BloodRequisitionMapper;
import com.ciphertext.opencarebackend.modules.blood.service.BloodRequisitionService;
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
@RequestMapping("/api/blood-requisitions")
@RequiredArgsConstructor
@Tag(name = "Blood Requisition Management", description = "API for creating, retrieving, updating, and deleting blood requisitions")
public class BloodRequisitionApiController {

    private final BloodRequisitionService bloodRequisitionService;
    private final BloodRequisitionMapper bloodRequisitionMapper;

    @Operation(
            summary = "Get paginated blood requisitions with filters",
            description = "Retrieves a paginated and filtered list of blood requisitions. Returns pagination metadata along with the results.",
            parameters = {
                    @Parameter(name = "page", description = "Page number (0-based)", example = "0"),
                    @Parameter(name = "size", description = "Number of items per page", example = "5"),
                    @Parameter(name = "requesterId", description = "Filter by requester ID"),
                    @Parameter(name = "patientName", description = "Filter by patient name"),
                    @Parameter(name = "minPatientAge", description = "Minimum patient age"),
                    @Parameter(name = "maxPatientAge", description = "Maximum patient age"),
                    @Parameter(name = "patientGenders", description = "Filter by patient genders (comma separated)"),
                    @Parameter(name = "bloodGroups", description = "Filter by blood groups (comma separated)"),
                    @Parameter(name = "bloodComponents", description = "Filter by blood components (comma separated)"),
                    @Parameter(name = "minQuantityBags", description = "Minimum quantity in bags"),
                    @Parameter(name = "maxQuantityBags", description = "Maximum quantity in bags"),
                    @Parameter(name = "neededByDateFrom", description = "Filter requests needed from this date (YYYY-MM-DD)"),
                    @Parameter(name = "neededByDateTo", description = "Filter requests needed until this date (YYYY-MM-DD)"),
                    @Parameter(name = "hospitalId", description = "Filter by hospital ID"),
                    @Parameter(name = "contactPhone", description = "Filter by contact phone"),
                    @Parameter(name = "districtId", description = "Filter by district ID"),
                    @Parameter(name = "upazilaId", description = "Filter by upazila ID"),
                    @Parameter(name = "statuses", description = "Filter by requisition statuses (comma separated)"),
                    @Parameter(name = "fulfilledDateFrom", description = "Filter by fulfilled date from (YYYY-MM-DD)"),
                    @Parameter(name = "fulfilledDateTo", description = "Filter by fulfilled date to (YYYY-MM-DD)"),
                    @Parameter(name = "isUrgent", description = "Filter urgent requests (needed within 48 hours)"),
                    @Parameter(name = "searchText", description = "Search in patient name, contact person, or description")
            }
    )
    @GetMapping("")
    public ResponseEntity<Map<String, Object>> getAllBloodRequisitionPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) Long requesterId,
            @RequestParam(required = false) String patientName,
            @RequestParam(required = false) Integer minPatientAge,
            @RequestParam(required = false) Integer maxPatientAge,
            @RequestParam(required = false) List<Gender> patientGenders,
            @RequestParam(required = false) List<BloodGroup> bloodGroups,
            @RequestParam(required = false) List<BloodComponent> bloodComponents,
            @RequestParam(required = false) Integer minQuantityBags,
            @RequestParam(required = false) Integer maxQuantityBags,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate neededByDateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate neededByDateTo,
            @RequestParam(required = false) Integer hospitalId,
            @RequestParam(required = false) String contactPhone,
            @RequestParam(required = false) Integer districtId,
            @RequestParam(required = false) Integer upazilaId,
            @RequestParam(required = false) List<RequisitionStatus> statuses,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fulfilledDateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fulfilledDateTo,
            @RequestParam(required = false) Boolean isUrgent,
            @RequestParam(required = false) String searchText) {

        BloodRequisitionFilter filter = BloodRequisitionFilter.builder()
                .requesterId(requesterId)
                .patientName(patientName)
                .minPatientAge(minPatientAge)
                .maxPatientAge(maxPatientAge)
                .patientGenders(patientGenders)
                .bloodGroups(bloodGroups)
                .bloodComponents(bloodComponents)
                .minQuantityBags(minQuantityBags)
                .maxQuantityBags(maxQuantityBags)
                .neededByDateFrom(neededByDateFrom)
                .neededByDateTo(neededByDateTo)
                .hospitalId(hospitalId)
                .contactPhone(contactPhone)
                .districtId(districtId)
                .upazilaId(upazilaId)
                .statuses(statuses)
                .fulfilledDateFrom(fulfilledDateFrom)
                .fulfilledDateTo(fulfilledDateTo)
                .isUrgent(isUrgent)
                .searchText(searchText)
                .build();

        Pageable pagingSort = PageRequest.of(page, size, Sort.Direction.DESC, "id");
        Page<BloodRequisition> requisitionPages = bloodRequisitionService.getPaginatedDataWithFilters(filter, pagingSort);
        Page<BloodRequisitionResponse> bloodRequisitionResponses = requisitionPages.map(bloodRequisitionMapper::toResponse);

        Map<String, Object> response = new HashMap<>();
        response.put("requisitions", bloodRequisitionResponses.getContent());
        response.put("currentPage", requisitionPages.getNumber());
        response.put("totalItems", requisitionPages.getTotalElements());
        response.put("totalPages", requisitionPages.getTotalPages());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get all blood requisitions", description = "Retrieves a complete list of all blood requisitions without pagination.")
    @GetMapping("/all")
    public ResponseEntity<List<BloodRequisitionResponse>> getAllBloodRequisition() {
        List<BloodRequisition> bloodRequisitions = bloodRequisitionService.getAllBloodRequisition();
        List<BloodRequisitionResponse> bloodRequisitionResponses = bloodRequisitions.stream()
                .map(bloodRequisitionMapper::toResponse)
                .toList();

        return ResponseEntity.ok(bloodRequisitionResponses);
    }

    @Operation(summary = "Get blood requisition by ID", description = "Retrieves a blood requisition by its unique ID")
    @GetMapping("/{id}")
    public ResponseEntity<BloodRequisitionResponse> getBloodRequisitionById(@PathVariable Long id){
        BloodRequisition bloodRequisition = bloodRequisitionService.getBloodRequisitionById(id);
        BloodRequisitionResponse bloodRequisitionResponse = bloodRequisitionMapper.toResponse(bloodRequisition);
        return ResponseEntity.ok(bloodRequisitionResponse);
    }

    @Operation(summary = "Create a new blood requisition", description = "Creates a new blood requisition with the provided details.")
    @PostMapping("")
    public ResponseEntity<BloodRequisitionResponse> createBloodRequisition(@Valid @RequestBody BloodRequisitionRequest bloodRequisitionRequest) {
        BloodRequisition bloodRequisition = bloodRequisitionMapper.toEntity(bloodRequisitionRequest);
        bloodRequisition = bloodRequisitionService.createBloodRequisition(bloodRequisition);
        BloodRequisitionResponse donationResponse = bloodRequisitionMapper.toResponse(bloodRequisition);
        return new ResponseEntity<>(donationResponse, HttpStatus.CREATED);
    }

    @Operation(summary = "Update a blood requisition", description = "Updates an existing blood requisition identified by its ID.")
    @PutMapping("/{id}")
    public ResponseEntity<BloodRequisitionResponse> updateBloodRequisitionById(@Valid @RequestBody BloodRequisitionRequest bloodRequisitionRequest, @PathVariable Long id){
        BloodRequisition bloodRequisition = bloodRequisitionMapper.toEntity(bloodRequisitionRequest);
        bloodRequisition = bloodRequisitionService.updateBloodRequisitionById(bloodRequisition, id);
        BloodRequisitionResponse donationResponse = bloodRequisitionMapper.toResponse(bloodRequisition);
        return ResponseEntity.ok(donationResponse);
    }

    @Operation(summary = "Delete a blood requisition", description = "Deletes a blood requisition identified by its ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBloodRequisitionById(@PathVariable Long id){
        bloodRequisitionService.deleteBloodRequisitionById(id);
        return ResponseEntity.noContent().build();
    }
}