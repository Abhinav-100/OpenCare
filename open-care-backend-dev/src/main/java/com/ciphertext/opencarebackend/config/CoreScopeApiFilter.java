package com.ciphertext.opencarebackend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class CoreScopeApiFilter extends OncePerRequestFilter {

    @Value("${app.core-scope-only:true}")
    private boolean coreScopeOnly;

    private static final List<String> DISABLED_API_PREFIXES = List.of(
            "/api/ambulance",
            "/api/ambulances",
            "/api/ambulance-types",
            "/api/associations",
            "/api/blood",
            "/api/blood-",
            "/api/blood-banks",
            "/api/blood-donations",
            "/api/blood-donors",
            "/api/blood-requisitions",
            "/api/degrees",
            "/api/github",
            "/api/health-vitals",
            "/api/hospital-amenity",
            "/api/hospital-medical-tests",
            "/api/institutions",
            "/api/medical-tests",
            "/api/medicines",
            "/api/modules/",
            "/api/nurses",
            "/api/social-organization",
            "/api/superadmin",
            "/api/synchronization"
    );

    @PostConstruct
    void logMode() {
        log.info("Core scope API filter active: app.core-scope-only={}", coreScopeOnly);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if (!coreScopeOnly) {
            filterChain.doFilter(request, response);
            return;
        }

        String path = request.getRequestURI();
        if (!path.startsWith("/api/")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (isDisabledApiPath(path)) {
            log.debug("Blocking non-core API path in core scope mode: {}", path);
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Endpoint disabled in core evaluation mode");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isDisabledApiPath(String path) {
        return DISABLED_API_PREFIXES.stream().anyMatch(path::startsWith);
    }
}