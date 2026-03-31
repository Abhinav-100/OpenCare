package com.ciphertext.opencarebackend.config;

import com.ciphertext.opencarebackend.entity.Profile;
import com.ciphertext.opencarebackend.modules.user.repository.ProfileRepository;
import com.ciphertext.opencarebackend.modules.user.service.UserActivityService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Filter to track user activity on every authenticated request.
 * Updates last_activity_time periodically to avoid excessive database writes.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserActivityTrackingFilter extends OncePerRequestFilter {

    private final UserActivityService userActivityService;
    private final ProfileRepository profileRepository;

    // Cache to track last update time for each user to avoid excessive DB writes
    private final ConcurrentHashMap<Long, Instant> lastActivityUpdateCache = new ConcurrentHashMap<>();

    // Update activity only if last update was more than 5 minutes ago
    private static final long UPDATE_THRESHOLD_MINUTES = 5;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {
            // Continue with the request
            filterChain.doFilter(request, response);

            // Track activity after successful request (avoid blocking the response)
            trackUserActivity(request);
        } catch (Exception e) {
            log.error("Error in UserActivityTrackingFilter", e);
            filterChain.doFilter(request, response);
        }
    }

    private void trackUserActivity(HttpServletRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.isAuthenticated()
                    && authentication.getPrincipal() instanceof Jwt jwt) {

                String keycloakUserId = jwt.getSubject();
                if (keycloakUserId != null) {
                    Optional<Profile> profileOpt = profileRepository.findByKeycloakUserId(keycloakUserId);

                    if (profileOpt.isPresent()) {
                        Long profileId = profileOpt.get().getId();

                        // Check if we should update (throttle updates)
                        if (shouldUpdateActivity(profileId)) {
                            userActivityService.updateLastActivity(profileId);
                            lastActivityUpdateCache.put(profileId, Instant.now());
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.debug("Failed to track user activity: {}", e.getMessage());
        }
    }

    private boolean shouldUpdateActivity(Long profileId) {
        Instant lastUpdate = lastActivityUpdateCache.get(profileId);
        if (lastUpdate == null) {
            return true;
        }

        Instant threshold = Instant.now().minus(UPDATE_THRESHOLD_MINUTES, ChronoUnit.MINUTES);
        return lastUpdate.isBefore(threshold);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        // Don't track activity for public endpoints, static resources, and actuator
        return path.startsWith("/api/auth/") ||
               path.startsWith("/api/public/") ||
               path.startsWith("/actuator/") ||
               path.startsWith("/v3/api-docs") ||
               path.startsWith("/swagger-ui") ||
               path.contains("/static/") ||
               path.contains("/favicon.ico");
    }
}
