package com.ciphertext.opencarebackend.modules.content.controller;

import com.ciphertext.opencarebackend.modules.content.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@Tag(name = "Unified Search", description = "General search across all healthcare entities")
public class SearchApiController {

    private final SearchService searchService;

    @Operation(
        summary = "General search across all domains",
        description = "Search across doctors, hospitals, and institutions with a single query. " +
                     "Results are grouped by domain and limited per domain for quick overview."
    )
    @GetMapping("/general")
    public ResponseEntity<Map<String, Object>> generalSearch(
            @Parameter(description = "Search term for name, speciality, location, etc.")
            @RequestParam String query,
            @Parameter(description = "Number of results per domain (default: 5)")
            @RequestParam(defaultValue = "5") int limitPerDomain,
            @Parameter(description = "Include doctors in search results")
            @RequestParam(defaultValue = "true") boolean includeDoctors,
            @Parameter(description = "Include hospitals in search results")
            @RequestParam(defaultValue = "true") boolean includeHospitals,
            @Parameter(description = "Include institutions in search results")
            @RequestParam(defaultValue = "true") boolean includeInstitutions) {

        log.info("General search for query: '{}' with limit: {}", query, limitPerDomain);
        
        Map<String, Object> results = searchService.generalSearch(
            query, limitPerDomain, includeDoctors, includeHospitals, includeInstitutions);
        
        return ResponseEntity.ok(results);
    }

    @Operation(
        summary = "Quick search suggestions",
        description = "Get search suggestions as user types for autocomplete functionality"
    )
    @GetMapping("/suggestions")
    public ResponseEntity<List<String>> getSearchSuggestions(
            @Parameter(description = "Partial search term")
            @RequestParam String partial,
            @Parameter(description = "Maximum number of suggestions")
            @RequestParam(defaultValue = "10") int limit) {

        log.info("Getting search suggestions for: '{}'", partial);
        
        List<String> suggestions = searchService.getSearchSuggestions(partial, limit);
        return ResponseEntity.ok(suggestions);
    }

    @Operation(
        summary = "Search statistics",
        description = "Get count of searchable items across all domains"
    )
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getSearchStats() {
        Map<String, Long> stats = searchService.getSearchStats();
        return ResponseEntity.ok(stats);
    }
}
