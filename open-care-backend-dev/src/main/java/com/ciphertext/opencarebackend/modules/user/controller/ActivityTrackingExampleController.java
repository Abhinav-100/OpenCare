package com.ciphertext.opencarebackend.modules.user.controller;

import com.ciphertext.opencarebackend.entity.Profile;
import com.ciphertext.opencarebackend.modules.user.service.ProfileService;
import com.ciphertext.opencarebackend.modules.user.service.UserActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Example controller demonstrating how to manually track user activity
 * for custom events or actions.
 *
 * This can be used as a reference for other controllers that need to
 * track specific user activities.
 */
@Slf4j
@RestController
@RequestMapping("/api/examples/activity-tracking")
@RequiredArgsConstructor
@Tag(name = "Activity Tracking Examples", description = "Example endpoints for manual activity tracking")
public class ActivityTrackingExampleController {

    private final UserActivityService userActivityService;
    private final ProfileService profileService;

    /**
     * Example: Update last activity time manually
     * Use this pattern when you want to explicitly track an activity
     * that's important for your business logic.
     */
    @Operation(summary = "Example: Manual activity update",
               description = "Demonstrates how to manually update user activity for custom events")
    @PostMapping("/manual-update")
    public ResponseEntity<String> manualActivityUpdate(@AuthenticationPrincipal Jwt jwt) {
        try {
            if (jwt != null) {
                String keycloakUserId = jwt.getSubject();
                Optional<Profile> profileOpt = Optional.ofNullable(profileService.getProfileByKeycloakUserId(keycloakUserId));

                if (profileOpt.isPresent()) {
                    Long profileId = profileOpt.get().getId();

                    // Manually update activity
                    userActivityService.updateLastActivity(profileId);

                    log.info("Manual activity update for profile: {}", profileId);
                    return ResponseEntity.ok("Activity tracked successfully");
                }
            }
            return ResponseEntity.badRequest().body("User not authenticated");
        } catch (Exception e) {
            log.error("Failed to track manual activity", e);
            return ResponseEntity.internalServerError().body("Failed to track activity");
        }
    }

    /**
     * Example: Helper method to extract profile ID from JWT
     * Reuse this pattern across your controllers for consistency
     */
    private Optional<Long> extractProfileId(Jwt jwt) {
        if (jwt == null) {
            return Optional.empty();
        }

        String keycloakUserId = jwt.getSubject();
        return Optional.ofNullable(profileService.getProfileByKeycloakUserId(keycloakUserId))
                .map(Profile::getId);
    }

    /**
     * Example: Track custom event with helper method
     */
    @Operation(summary = "Example: Custom event tracking",
               description = "Demonstrates tracking a custom event using helper method")
    @PostMapping("/custom-event")
    public ResponseEntity<String> trackCustomEvent(@AuthenticationPrincipal Jwt jwt) {
        try {
            Optional<Long> profileIdOpt = extractProfileId(jwt);

            if (profileIdOpt.isPresent()) {
                Long profileId = profileIdOpt.get();

                // Your custom business logic here
                log.info("Processing custom event for profile: {}", profileId);

                // Track the activity
                userActivityService.updateLastActivity(profileId);

                return ResponseEntity.ok("Custom event processed and tracked");
            }
            return ResponseEntity.badRequest().body("User not authenticated");
        } catch (Exception e) {
            log.error("Failed to process custom event", e);
            return ResponseEntity.internalServerError().body("Failed to process event");
        }
    }

    /**
     * Example: Track activity only on specific conditions
     */
    @Operation(summary = "Example: Conditional activity tracking",
               description = "Track activity only when certain conditions are met")
    @PostMapping("/conditional-tracking")
    public ResponseEntity<String> conditionalTracking(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "false") boolean trackActivity) {
        try {
            // Your business logic
            String result = "Processed request";

            // Only track if specified
            if (trackActivity) {
                extractProfileId(jwt).ifPresent(profileId -> {
                    userActivityService.updateLastActivity(profileId);
                    log.debug("Activity tracked for profile: {}", profileId);
                });
            }

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Failed in conditional tracking", e);
            return ResponseEntity.internalServerError().body("Failed to process");
        }
    }
}
