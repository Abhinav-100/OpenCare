package com.ciphertext.opencarebackend.modules.catalog.controller;
import com.ciphertext.opencarebackend.modules.catalog.dto.filter.TagFilter;
import com.ciphertext.opencarebackend.modules.catalog.dto.request.TagRequest;
import com.ciphertext.opencarebackend.modules.catalog.dto.response.TagResponse;
import com.ciphertext.opencarebackend.entity.Tag;
import com.ciphertext.opencarebackend.exception.ResourceNotFoundException;
import com.ciphertext.opencarebackend.mapper.TagMapper;
import com.ciphertext.opencarebackend.modules.catalog.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Tag Management", description = "API for managing tags including creation, retrieval, updating and deletion of tag records")
public class TagApiController {

    private final TagService tagService;
    private final TagMapper tagMapper;

    @Operation(
        summary = "Get all tags",
        description = "Retrieves a paginated list of all tags in the system."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved tags",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TagResponse.class)))
    })
    @GetMapping
    public ResponseEntity<Page<TagResponse>> getAllTags(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field", example = "name")
            @RequestParam(defaultValue = "name") String sortBy,
            @Parameter(description = "Sort direction", example = "asc")
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.info("Getting all tags - page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Tag> tags = tagService.getAllTags(pageable);
        Page<TagResponse> tagResponses = tags.map(tagMapper::toResponse);

        return ResponseEntity.ok(tagResponses);
    }

    @Operation(
        summary = "Search and filter tags",
        description = "Retrieves tags based on filter criteria with pagination."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved filtered tags",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TagResponse.class)))
    })
    @GetMapping("/search")
    public ResponseEntity<Page<TagResponse>> searchTags(
            @Parameter(description = "Tag name filter") @RequestParam(required = false) String name,
            @Parameter(description = "Tag category filter") @RequestParam(required = false) String category,
            @Parameter(description = "General search query for name or display name") @RequestParam(required = false) String query,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field", example = "name")
            @RequestParam(defaultValue = "name") String sortBy,
            @Parameter(description = "Sort direction", example = "asc")
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.info("Searching tags with filters - name: {}, category: {}, query: {}", name, category, query);

        TagFilter filter = new TagFilter(name, category, query);
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Tag> tags = tagService.getFilteredTags(filter, pageable);
        Page<TagResponse> tagResponses = tags.map(tagMapper::toResponse);

        return ResponseEntity.ok(tagResponses);
    }

    @Operation(
        summary = "Get tag by ID",
        description = "Retrieves a specific tag by its ID."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved tag",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TagResponse.class))),
        @ApiResponse(responseCode = "404", description = "Tag not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TagResponse> getTagById(
            @Parameter(description = "Tag ID", required = true) @PathVariable Integer id)
            throws ResourceNotFoundException {

        log.info("Getting tag by ID: {}", id);

        Tag tag = tagService.getTagById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Tag not found with ID: " + id));

        TagResponse tagResponse = tagMapper.toResponse(tag);
        return ResponseEntity.ok(tagResponse);
    }

    @Operation(
        summary = "Get tags by category",
        description = "Retrieves all tags belonging to a specific category."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved tags",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TagResponse.class)))
    })
    @GetMapping("/category/{category}")
    public ResponseEntity<List<TagResponse>> getTagsByCategory(
            @Parameter(description = "Tag category", required = true) @PathVariable String category) {

        log.info("Getting tags by category: {}", category);

        List<Tag> tags = tagService.getTagsByCategory(category);
        List<TagResponse> tagResponses = tags.stream()
            .map(tagMapper::toResponse)
            .collect(Collectors.toList());

        return ResponseEntity.ok(tagResponses);
    }

    @Operation(
        summary = "Fuzzy search tags",
        description = "Performs a fuzzy search for tags using PostgreSQL trigram similarity."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved tags",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TagResponse.class)))
    })
    @GetMapping("/fuzzy-search")
    public ResponseEntity<List<TagResponse>> fuzzySearchTags(
            @Parameter(description = "Search query", required = true) @RequestParam String q,
            @Parameter(description = "Maximum number of results", example = "10")
            @RequestParam(defaultValue = "10") int limit) {

        log.info("Performing fuzzy search for tags with query: {} and limit: {}", q, limit);

        List<Tag> tags = tagService.fuzzySearchTags(q, limit);
        List<TagResponse> tagResponses = tags.stream()
            .map(tagMapper::toResponse)
            .collect(Collectors.toList());

        return ResponseEntity.ok(tagResponses);
    }

    @Operation(
        summary = "Create a new tag",
        description = "Creates a new tag with the provided information."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Tag created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TagResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input or tag with same name already exists"),
        @ApiResponse(responseCode = "422", description = "Validation failed")
    })
    @PostMapping
    public ResponseEntity<TagResponse> createTag(@Valid @RequestBody TagRequest tagRequest) {
        log.info("Creating new tag: {}", tagRequest);

        Tag createdTag = tagService.createTag(tagRequest);
        TagResponse tagResponse = tagMapper.toResponse(createdTag);

        return ResponseEntity.status(HttpStatus.CREATED).body(tagResponse);
    }

    @Operation(
        summary = "Update an existing tag",
        description = "Updates the details of an existing tag identified by its ID."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tag updated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TagResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input or tag with same name already exists"),
        @ApiResponse(responseCode = "404", description = "Tag not found"),
        @ApiResponse(responseCode = "422", description = "Validation failed")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TagResponse> updateTag(
            @Valid @RequestBody TagRequest tagRequest,
            @Parameter(description = "Tag ID", required = true) @PathVariable Integer id)
            throws ResourceNotFoundException {

        log.info("Updating tag with ID: {}", id);

        Tag updatedTag = tagService.updateTag(tagRequest, id);
        TagResponse tagResponse = tagMapper.toResponse(updatedTag);

        return ResponseEntity.ok(tagResponse);
    }

    @Operation(
        summary = "Delete a tag",
        description = "Deletes a tag record identified by its ID."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Tag deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Tag not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(
            @Parameter(description = "Tag ID", required = true) @PathVariable Integer id)
            throws ResourceNotFoundException {

        log.info("Deleting tag with ID: {}", id);

        tagService.deleteTagById(id);

        return ResponseEntity.noContent().build();
    }
}
