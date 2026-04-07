package com.ciphertext.opencarebackend.modules.auth.controller;

import com.ciphertext.opencarebackend.entity.Profile;
import com.ciphertext.opencarebackend.exception.BadRequestException;
import com.ciphertext.opencarebackend.modules.auth.dto.request.DoctorSelfRegistrationRequest;
import com.ciphertext.opencarebackend.modules.auth.dto.request.ForgotPasswordRequest;
import com.ciphertext.opencarebackend.modules.auth.dto.request.LoginRequest;
import com.ciphertext.opencarebackend.modules.auth.dto.request.RefreshTokenRequest;
import com.ciphertext.opencarebackend.modules.auth.dto.request.RegistrationRequest;
import com.ciphertext.opencarebackend.modules.auth.dto.response.RegistrationResponse;
import com.ciphertext.opencarebackend.modules.auth.dto.response.TokenResponse;
import com.ciphertext.opencarebackend.modules.auth.service.AuthService;
import com.ciphertext.opencarebackend.modules.auth.service.KeycloakService;
import com.ciphertext.opencarebackend.modules.shared.dto.request.LogoutRequest;
import com.ciphertext.opencarebackend.modules.user.service.ProfileService;
import com.ciphertext.opencarebackend.modules.user.service.UserActivityService;
import com.ciphertext.opencarebackend.util.JwtUtil;
import com.ciphertext.opencarebackend.util.RequestMetadataUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;




@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API for user authentication and authorization")
/**
 * Flow note: AuthApiController belongs to the authentication module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public class AuthApiController {

    private final AuthService authService;
    private final KeycloakService keycloakService;
    private final UserActivityService userActivityService;
    private final ProfileService profileService;
    private final JwtUtil jwtUtil;
    private final RequestMetadataUtil requestMetadataUtil;

    @Operation(summary = "User login", description = "Authenticate user and return access and refresh tokens.")
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        TokenResponse tokenResponse = keycloakService.login(
                loginRequest.getUsername(),
                loginRequest.getPassword()).block();

        if (tokenResponse == null || tokenResponse.getAccess_token() == null) {
            throw new BadRequestException("Invalid authentication response");
        }

        // Track login activity
        try {
            String keycloakUserId = jwtUtil.extractUserId(tokenResponse.getAccess_token());
            Optional<Profile> profileOpt = Optional.ofNullable(profileService.getProfileByKeycloakUserId(keycloakUserId));

            if (profileOpt.isPresent()) {
                // Extract metadata before async call to avoid request recycling issues
                String clientIp = requestMetadataUtil.extractClientIp(request);
                String device = requestMetadataUtil.extractDevice(request);
                String browser = requestMetadataUtil.extractBrowser(request);
                String operatingSystem = requestMetadataUtil.extractOperatingSystem(request);

                log.info("Login metadata - IP: {}, Device: {}, Browser: {}, Operating System: {}", clientIp, device, browser, operatingSystem);
                userActivityService.updateOnLogin(profileOpt.get().getId(), clientIp, device, browser, operatingSystem);
            }
        } catch (Exception e) {
            log.warn("Failed to track login activity: {}", e.getMessage());
        }

        return ResponseEntity.ok(tokenResponse);
    }

    @Operation(summary = "Test User-Agent parsing", description = "Debug endpoint to test device and browser detection.")
    @GetMapping("/debug/user-agent")
    public ResponseEntity<Map<String, String>> debugUserAgent(HttpServletRequest request) {
        Map<String, String> result = new HashMap<>();
        result.put("userAgent", request.getHeader("User-Agent"));
        result.put("clientIp", requestMetadataUtil.extractClientIp(request));
        result.put("device", requestMetadataUtil.extractDevice(request));
        result.put("browser", requestMetadataUtil.extractBrowser(request));
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "User registration", description = "Register a new user in the system.")
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegistrationRequest registrationRequest,
            HttpServletRequest request) {
        log.info("Registration request received for email: {}", registrationRequest.getEmail());

        RegistrationResponse response = authService.registerUser(registrationRequest);

        // Track registration activity
        if (response.getUserId() != null && !response.getUserId().isBlank()) {
            trackRegistrationActivity(response.getUserId(), request);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Doctor self-registration", description = "Register a doctor account and create a pending doctor profile for admin approval.")
    @PostMapping("/register/doctor")
    public ResponseEntity<RegistrationResponse> registerDoctor(
            @Valid @RequestBody DoctorSelfRegistrationRequest registrationRequest,
            HttpServletRequest request) {
        log.info("Doctor self-registration request received for email: {}", registrationRequest.getEmail());

        RegistrationResponse response = authService.registerDoctor(registrationRequest);

        if (response.getUserId() != null && !response.getUserId().isBlank()) {
            trackRegistrationActivity(response.getUserId(), request);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Track registration activity for a new user
     */
    private void trackRegistrationActivity(String userId, HttpServletRequest request) {
        try {
            Optional<Profile> profileOpt = Optional.ofNullable(profileService.getProfileByKeycloakUserId(userId));
            if (profileOpt.isPresent()) {
                // Extract metadata from request
                String clientIp = requestMetadataUtil.extractClientIp(request);
                String device = requestMetadataUtil.extractDevice(request);
                String browser = requestMetadataUtil.extractBrowser(request);
                String operatingSystem = requestMetadataUtil.extractOperatingSystem(request);

                log.info("Tracking registration activity - IP: {}, Device: {}, Browser: {}, OS: {}",
                        clientIp, device, browser, operatingSystem);

                userActivityService.trackRegistration(
                        profileOpt.get().getId(),
                        clientIp,
                        device,
                        browser,
                        operatingSystem
                );
            }
        } catch (Exception e) {
            log.warn("Failed to track registration activity: {}", e.getMessage());
            // Don't fail the registration if tracking fails
        }
    }

    @Operation(summary = "Refresh token", description = "Refresh access token using a valid refresh token.")
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshRequest) {
        TokenResponse tokenResponse = keycloakService.refreshToken(refreshRequest.getRefreshToken()).block();
        if (tokenResponse == null || tokenResponse.getAccess_token() == null) {
            throw new BadRequestException("Invalid refresh response");
        }
        return ResponseEntity.ok(tokenResponse);
    }

    @Operation(summary = "User logout", description = "Logout user and invalidate the refresh token.")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest logoutRequest, @AuthenticationPrincipal Jwt jwt) {
        keycloakService.logout(logoutRequest.getRefreshToken()).block();

        // Track logout activity
        if (jwt != null) {
            try {
                String keycloakUserId = jwt.getSubject();
                Optional<Profile> profileOpt = Optional.ofNullable(profileService.getProfileByKeycloakUserId(keycloakUserId));

                if (profileOpt.isPresent()) {
                    userActivityService.updateOnLogout(profileOpt.get().getId());
                }
            } catch (Exception e) {
                log.warn("Failed to track logout activity: {}", e.getMessage());
            }
        }

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Forgot password", description = "Initiate password reset process for a user.")
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        keycloakService.resetPassword(forgotPasswordRequest.getEmail()).block();
        return ResponseEntity.ok("Password reset email sent");
    }

    @Operation(summary = "Get user roles", description = "Retrieve roles assigned to the authenticated user.")
    @GetMapping("/roles")
    public ResponseEntity<Map<String, Object>> getUserRoles(@AuthenticationPrincipal Jwt jwt) {
        if (jwt == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Authentication required"));
        }

        String userId = jwt.getClaimAsString("sub");

        List<String> roleNames = keycloakService.getUserRealmRoleNames(userId).block();
        Map<String, Object> response = new HashMap<>();
        response.put("roles", roleNames);

        return ResponseEntity.ok(response);
    }
}