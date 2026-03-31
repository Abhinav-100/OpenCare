package com.ciphertext.opencarebackend.modules.blood.controller;
import com.ciphertext.opencarebackend.modules.blood.dto.filter.BloodBankFilter;
import com.ciphertext.opencarebackend.entity.BloodBank;
import com.ciphertext.opencarebackend.entity.BloodInventory;
import com.ciphertext.opencarebackend.modules.blood.service.BloodBankService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Slf4j
@RestController
@RequestMapping("/api/blood-banks")
@RequiredArgsConstructor
@Tag(name = "Blood Bank Management", description = "API for managing blood banks and blood inventory")
public class BloodBankApiController {

    private final BloodBankService bloodBankService;

    @Operation(
            summary = "Get paginated blood banks",
            description = "Retrieves a paginated list of blood banks with optional filters",
            parameters = {
                    @Parameter(name = "page", description = "Page number (0-based)", example = "0"),
                    @Parameter(name = "size", description = "Number of items per page", example = "10")
            }
    )
    @GetMapping("")
    public ResponseEntity<Map<String, Object>> getAllBloodBanksPage(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer hospitalId,
            @RequestParam(required = false) Boolean isAlwaysOpen,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) String bloodGroupNeeded,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "ASC") String direction) {

        Sort.Direction sortDir = Sort.Direction.fromOptionalString(direction.toUpperCase()).orElse(Sort.Direction.ASC);
        Pageable pagingSort = PageRequest.of(page, size, Sort.by(sortDir, sort));

        BloodBankFilter filter = BloodBankFilter.builder()
                .name(name)
                .hospitalId(hospitalId)
                .isAlwaysOpen(isAlwaysOpen)
                .isActive(isActive)
                .bloodGroupNeeded(bloodGroupNeeded)
                .build();

        Page<BloodBank> pageBloodBanks = bloodBankService.getPaginatedDataWithFilters(filter, pagingSort);

        Map<String, Object> response = new HashMap<>();
        response.put("bloodBanks", pageBloodBanks.getContent());
        response.put("currentPage", pageBloodBanks.getNumber());
        response.put("totalItems", pageBloodBanks.getTotalElements());
        response.put("totalPages", pageBloodBanks.getTotalPages());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get all active blood banks", description = "Retrieves all active blood banks")
    @GetMapping("/active")
    public ResponseEntity<List<BloodBank>> getAllActiveBloodBanks() {
        List<BloodBank> bloodBanks = bloodBankService.getAllActiveBloodBanks();
        return ResponseEntity.ok(bloodBanks);
    }

    @Operation(summary = "Get blood bank by ID", description = "Retrieves a blood bank by its unique ID")
    @GetMapping("/{id}")
    public ResponseEntity<BloodBank> getBloodBankById(@PathVariable Integer id) {
        BloodBank bloodBank = bloodBankService.getBloodBankById(id);
        return ResponseEntity.ok(bloodBank);
    }

    @Operation(summary = "Get blood bank inventory", description = "Retrieves all blood inventory for a specific blood bank")
    @GetMapping("/{id}/inventory")
    public ResponseEntity<List<BloodInventory>> getBloodBankInventory(@PathVariable Integer id) {
        List<BloodInventory> inventory = bloodBankService.getBloodBankInventory(id);
        return ResponseEntity.ok(inventory);
    }

    @Operation(summary = "Get available blood inventory", description = "Retrieves available blood (units > 0) for a specific blood bank")
    @GetMapping("/{id}/inventory/available")
    public ResponseEntity<List<BloodInventory>> getAvailableInventory(@PathVariable Integer id) {
        List<BloodInventory> inventory = bloodBankService.getAvailableInventory(id);
        return ResponseEntity.ok(inventory);
    }

    @Operation(summary = "Create blood bank", description = "Creates a new blood bank")
    @PostMapping
    public ResponseEntity<BloodBank> createBloodBank(@RequestBody BloodBank bloodBank) {
        BloodBank created = bloodBankService.createBloodBank(bloodBank);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @Operation(summary = "Update blood bank", description = "Updates an existing blood bank")
    @PutMapping("/{id}")
    public ResponseEntity<BloodBank> updateBloodBank(
            @RequestBody BloodBank bloodBank,
            @PathVariable Integer id) {
        BloodBank updated = bloodBankService.updateBloodBank(bloodBank, id);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Delete blood bank", description = "Deletes a blood bank by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBloodBank(@PathVariable Integer id) {
        bloodBankService.deleteBloodBankById(id);
        return ResponseEntity.noContent().build();
    }
}
