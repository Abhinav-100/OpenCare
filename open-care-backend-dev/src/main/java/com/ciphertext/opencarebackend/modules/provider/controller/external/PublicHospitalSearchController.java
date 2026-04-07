package com.ciphertext.opencarebackend.modules.provider.controller.external;
import com.ciphertext.opencarebackend.modules.provider.dto.elasticsearch.HospitalDocument;
import com.ciphertext.opencarebackend.modules.provider.dto.elasticsearch.filter.HospitalSearchFilter;
import com.ciphertext.opencarebackend.modules.provider.service.elasticsearch.HospitalSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/public/hospitals")
@RequiredArgsConstructor
@Tag(name = "Public Hospital Search", description = "API for publicly searching hospital information")
/**
 * Flow note: PublicHospitalSearchController belongs to the provider doctor/hospital module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public class PublicHospitalSearchController {

    private final HospitalSearchService hospitalSearchService;

    @Operation(
            summary = "Search hospitals",
            description = "Search hospitals by various criteria including text search, location, and filters"
    )
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchHospitals(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) Integer districtId,
            @RequestParam(required = false) Integer upazilaId,
            @RequestParam(required = false) String hospitalType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        HospitalSearchFilter filter = HospitalSearchFilter.builder()
                .searchTerm(searchTerm)
                .districtId(districtId)
                .upazilaId(upazilaId)
                .hospitalType(hospitalType)
                .build();

        Pageable pageable = PageRequest.of(page, size);
        Page<HospitalDocument> hospitalDocuments = hospitalSearchService.searchHospitals(filter, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("hospitals", hospitalDocuments.getContent());
        response.put("currentPage", hospitalDocuments.getNumber());
        response.put("totalItems", hospitalDocuments.getTotalElements());
        response.put("totalPages", hospitalDocuments.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Find nearby hospitals",
            description = "Find hospitals near a specific location within a given distance"
    )
    @GetMapping("/nearby")
    public ResponseEntity<Map<String, Object>> findNearbyHospitals(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "5") Double distance,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        GeoPoint location = new GeoPoint(latitude, longitude);
        Pageable pageable = PageRequest.of(page, size);

        Page<HospitalDocument> hospitalDocuments =
                hospitalSearchService.findNearbyHospitals(location, distance, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("hospitals", hospitalDocuments.getContent());
        response.put("currentPage", hospitalDocuments.getNumber());
        response.put("totalItems", hospitalDocuments.getTotalElements());
        response.put("totalPages", hospitalDocuments.getTotalPages());

        return ResponseEntity.ok(response);
    }
}
