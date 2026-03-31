package com.ciphertext.opencarebackend.modules.clinical.controller;
import com.ciphertext.opencarebackend.modules.clinical.dto.response.MedicineResponse;
import com.ciphertext.opencarebackend.entity.Medicine;
import com.ciphertext.opencarebackend.exception.ResourceNotFoundException;
import com.ciphertext.opencarebackend.mapper.MedicineMapper;
import com.ciphertext.opencarebackend.modules.clinical.service.MedicineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Slf4j
@RestController
@RequestMapping("/api/medicines")
@RequiredArgsConstructor
@Tag(name = "Medicine Management", description = "API for searching and retrieving medicine information")
public class MedicineApiController {

    private final MedicineService medicineService;
    private final MedicineMapper medicineMapper;

    @GetMapping("/basic")
    @Operation(summary = "Basic search for medicines", description = "Performs a basic paginated search for medicines by term")
    public ResponseEntity<Map<String, Object>> getBasicMedicinesPage(
            @RequestParam(required = false) String term,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Pageable pagingSort = PageRequest.of(page, size);

        Page<Medicine> pageMedicines = medicineService.basicSearch(term, pagingSort);

        Page<MedicineResponse> pageMedicineResponses = pageMedicines.map(medicineMapper::toResponse);

        Map<String, Object> response = new HashMap<>();
        response.put("medicines", pageMedicineResponses.getContent());
        response.put("currentPage", pageMedicines.getNumber());
        response.put("totalItems", pageMedicines.getTotalElements());
        response.put("totalPages", pageMedicines.getTotalPages());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/full-text")
    @Operation(summary = "Full-text search for medicines", description = "Performs a full-text paginated search for medicines by term")
    public ResponseEntity<Map<String, Object>> getFullTextMedicinesPage(
            @RequestParam(required = false) String term,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Pageable pagingSort = PageRequest.of(page, size);

        Page<Medicine> pageMedicines = medicineService.fullTextSearch(term, pagingSort);

        Page<MedicineResponse> pageMedicineResponses = pageMedicines.map(medicineMapper::toResponse);

        Map<String, Object> response = new HashMap<>();
        response.put("medicines", pageMedicineResponses.getContent());
        response.put("currentPage", pageMedicines.getNumber());
        response.put("totalItems", pageMedicines.getTotalElements());
        response.put("totalPages", pageMedicines.getTotalPages());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/advanced")
    @Operation(summary = "Advanced search for medicines", description = "Performs an advanced paginated search for medicines by brand, generic, manufacturer, or type")
    public ResponseEntity<Map<String, Object>> getAdvancedMedicinesPage(
            @RequestParam(required = false) String brandName,
            @RequestParam(required = false) String generic,
            @RequestParam(required = false) String manufacturer,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Pageable pagingSort = PageRequest.of(page, size);

        Page<Medicine> pageMedicines =
                medicineService.advancedSearch(brandName, generic, manufacturer, type, pagingSort);

        Page<MedicineResponse> pageMedicineResponses = pageMedicines.map(medicineMapper::toResponse);

        Map<String, Object> response = new HashMap<>();
        response.put("medicines", pageMedicineResponses.getContent());
        response.put("currentPage", pageMedicines.getNumber());
        response.put("totalItems", pageMedicines.getTotalElements());
        response.put("totalPages", pageMedicines.getTotalPages());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get medicine by ID", description = "Retrieves a medicine by its unique ID")
    public ResponseEntity<MedicineResponse> getMedicineById(@PathVariable int id)
            throws ResourceNotFoundException {
        log.info("Retrieving medicine with ID: {}", id);

        Medicine medicine = medicineService.getMedicineById(id);
        MedicineResponse medicineResponse = medicineMapper.toResponse(medicine);

        return ResponseEntity.ok(medicineResponse);
    }
}
