package com.ciphertext.opencarebackend.modules.platform.controller;

import com.ciphertext.opencarebackend.modules.platform.service.GitHubService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/github")
@RequiredArgsConstructor
@Tag(name = "GitHub Public API", description = "API for fetching public GitHub contributors and related data")
public class GitHubController {

    private final GitHubService gitHubService;

    @Operation(
            summary = "Get GitHub contributors",
            description = "Fetches the list of contributors from the project's GitHub repository.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Contributors fetched successfully"),
                    @ApiResponse(responseCode = "500", description = "Failed to fetch contributors")
            }
    )
    @GetMapping(value = "/contributors", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getContributors() {
        try {
            String jsonResponse = gitHubService.fetchContributors();
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(jsonResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Failed to fetch contributors\"}");
        }
    }

    @Scheduled(fixedRate = 30 * 60 * 1000)
    @CacheEvict(value = "githubContributors", allEntries = true)
    public void evictContributorsCache() { }

    @Scheduled(fixedRate = 10 * 60 * 1000)
    @CacheEvict(value = "latestBlogPosts", allEntries = true)
    public void evictBlogCache() { }

    @Scheduled(fixedRate = 60 * 60 * 1000)
    @CacheEvict(value = "topDoctors", allEntries = true)
    public void evictDoctorCache() { }
}
