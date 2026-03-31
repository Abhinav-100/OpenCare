package com.ciphertext.opencarebackend.modules.user.controller;
import com.ciphertext.opencarebackend.modules.user.dto.response.UserActivityResponse;
import com.ciphertext.opencarebackend.entity.UserActivity;
import com.ciphertext.opencarebackend.mapper.UserActivityMapper;
import com.ciphertext.opencarebackend.modules.user.service.UserActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/user-activities")
@RequiredArgsConstructor
@Tag(name = "User Activity Management", description = "API for retrieving user activity data including login history, session information, and ad interactions")
public class UserActivityApiController {

    private final UserActivityService userActivityService;
    private final UserActivityMapper userActivityMapper;

    @Operation(
            summary = "Get paginated user activities",
            description = "Retrieves a paginated list of user activities. Returns pagination metadata along with the results.",
            parameters = {
                    @Parameter(name = "page", description = "Page number (0-based)", example = "0"),
                    @Parameter(name = "size", description = "Number of items per page", example = "10"),
                    @Parameter(name = "sort", description = "Field to sort by", example = "lastActivityTime"),
                    @Parameter(name = "direction", description = "Sort direction (ASC or DESC)", example = "DESC")
            }
    )
    @GetMapping("")
    public ResponseEntity<Map<String, Object>> getAllUserActivities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "lastActivityTime") String sort,
            @RequestParam(defaultValue = "DESC") String direction) {

        log.info("GET /api/user-activities - page: {}, size: {}, sort: {}, direction: {}",
                page, size, sort, direction);

        Sort.Direction sortDirection = direction.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        Page<UserActivity> userActivitiesPage = userActivityService.getAllUserActivities(pageable);
        List<UserActivityResponse> userActivities = userActivitiesPage.getContent().stream()
                .map(userActivityMapper::toResponse)
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("userActivities", userActivities);
        response.put("currentPage", userActivitiesPage.getNumber());
        response.put("totalItems", userActivitiesPage.getTotalElements());
        response.put("totalPages", userActivitiesPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get user activity by profile ID",
            description = "Retrieves detailed user activity information for a specific profile ID.",
            parameters = {
                    @Parameter(name = "profileId", description = "The profile ID", example = "1", required = true)
            }
    )
    @GetMapping("/{profileId}")
    public ResponseEntity<UserActivityResponse> getUserActivityById(@PathVariable Long profileId) {
        log.info("GET /api/user-activities/{} - Fetching user activity", profileId);

        UserActivity userActivity = userActivityService.getUserActivityById(profileId);
        UserActivityResponse response = userActivityMapper.toResponse(userActivity);

        return ResponseEntity.ok(response);
    }
}
