package com.ciphertext.opencarebackend.modules.provider.controller.external;
import com.ciphertext.opencarebackend.modules.provider.dto.elasticsearch.DoctorDocument;
import com.ciphertext.opencarebackend.modules.provider.dto.elasticsearch.filter.DoctorSearchFilter;
import com.ciphertext.opencarebackend.modules.provider.service.elasticsearch.DoctorSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/public/doctors")
@RequiredArgsConstructor
@Tag(name = "Public Doctor Search", description = "API for publicly searching doctor information")
public class PublicDoctorSearchController {

    private final DoctorSearchService doctorSearchService;

    @Operation(
            summary = "Search doctors",
            description = "Search doctors by text and location-based filters"
    )
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchDoctors(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) Integer districtId,
            @RequestParam(required = false) Integer upazilaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        DoctorSearchFilter filter = DoctorSearchFilter.builder()
                .searchTerm(searchTerm)
                .districtId(districtId)
                .upazilaId(upazilaId)
                .build();

        Pageable pageable = PageRequest.of(page, size);
        Page<DoctorDocument> doctorDocuments = doctorSearchService.searchDoctors(filter, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("doctors", doctorDocuments.getContent());
        response.put("currentPage", doctorDocuments.getNumber());
        response.put("totalItems", doctorDocuments.getTotalElements());
        response.put("totalPages", doctorDocuments.getTotalPages());

        return ResponseEntity.ok(response);
    }
}
