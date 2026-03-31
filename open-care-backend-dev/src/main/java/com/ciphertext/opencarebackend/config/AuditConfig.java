package com.ciphertext.opencarebackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;

@Configuration
public class AuditConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return new AuditorAwareImpl();
    }
}

class AuditorAwareImpl implements AuditorAware<String> {

    private static final String ANONYMOUS = "anonymous";

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return Optional.of(ANONYMOUS);
        }

        Object principal = authentication.getPrincipal();

        // Spring Security user
        if (principal instanceof UserDetails user) {
            return Optional.ofNullable(user.getUsername());
        }

        // Resource Server (JWT)
        if (principal instanceof Jwt jwt) {
            String username = firstNonBlank(
                    jwt.getClaimAsString("preferred_username"),
                    jwt.getClaimAsString("username"),
                    jwt.getClaimAsString("email"),
                    jwt.getSubject(),
                    authentication.getName()
            );
            return Optional.ofNullable(username);
        }

        // OAuth2/OIDC client (DefaultOAuth2User / OidcUser)
        if (principal instanceof OAuth2AuthenticatedPrincipal oauth2) {
            String username = firstNonBlank(
                    oauth2.getAttribute("preferred_username"),
                    oauth2.getAttribute("username"),
                    oauth2.getAttribute("email"),
                    oauth2.getAttribute("sub"),
                    authentication.getName()
            );
            return Optional.ofNullable(username);
        }

        // Fallback: use authentication name or principal string
        String username = firstNonBlank(authentication.getName(),
                principal != null ? principal.toString() : null);
        return Optional.ofNullable(username);
    }

    private static String firstNonBlank(String... values) {
        if (values == null) return null;
        for (String v : values) {
            if (v != null && !v.isBlank()) return v;
        }
        return null;
    }
}