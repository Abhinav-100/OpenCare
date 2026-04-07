package com.ciphertext.opencarebackend.config;

import com.ciphertext.opencarebackend.modules.auth.service.KeycloakService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

    private final KeycloakService keycloakService;
    private final CoreScopeApiFilter coreScopeApiFilter;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    public SecurityConfig(KeycloakService keycloakService, CoreScopeApiFilter coreScopeApiFilter) {
        this.keycloakService = keycloakService;
        this.coreScopeApiFilter = coreScopeApiFilter;
    }

    @Bean
    @Order(2)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Main request-security pipeline:
        // - stateless JWT auth
        // - endpoint-level authorization rules
        // - custom scope filter before username/password filter
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/public/**").permitAll()
                    .requestMatchers(HttpMethod.POST,
                        "/api/auth/login",
                        "/api/auth/register",
                        "/api/auth/register/doctor",
                        "/api/auth/refresh",
                        "/api/auth/forgot-password").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/auth/logout").authenticated()
                    .requestMatchers(HttpMethod.GET, "/api/auth/roles").authenticated()
                    .requestMatchers(HttpMethod.GET, "/api/auth/debug/user-agent").authenticated()

                        .requestMatchers("/api/files/**").permitAll()

                        // Public GET access for browsing healthcare resources
                        .requestMatchers(HttpMethod.GET, "/api/hospitals/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/doctors/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/ambulances/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/blood-banks/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/institutions/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/medical-specialities/**").permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/synchronization/**").hasAuthority("sync-data")

                        .requestMatchers(HttpMethod.POST, "/api/user/*/assign-user-type").hasAuthority("assign-user-role")
                        .requestMatchers(HttpMethod.POST, "/api/user/*/remove-user-type").hasAuthority("remove-user-role")
                        .requestMatchers(HttpMethod.POST, "/api/user/doctors/*/create-user").hasAuthority("create-doctor-user")

                        .requestMatchers(HttpMethod.POST, "/api/doctors").hasAnyAuthority("create-doctor", "admin", "super-admin", "role_admin", "role_super_admin")
                        .requestMatchers(HttpMethod.PUT, "/api/doctors/*").hasAnyAuthority("update-doctor", "admin", "super-admin", "role_admin", "role_super_admin")
                        .requestMatchers(HttpMethod.PATCH, "/api/doctors/*/verify").hasAnyAuthority("update-doctor", "admin", "super-admin", "role_admin", "role_super_admin")
                        .requestMatchers(HttpMethod.PATCH, "/api/doctors/*/activate").hasAnyAuthority("update-doctor", "admin", "super-admin", "role_admin", "role_super_admin")
                        .requestMatchers(HttpMethod.PATCH, "/api/doctors/*/deactivate").hasAnyAuthority("update-doctor", "admin", "super-admin", "role_admin", "role_super_admin")
                        .requestMatchers(HttpMethod.PATCH, "/api/doctors/*/approve").hasAnyAuthority("update-doctor", "admin", "super-admin", "role_admin", "role_super_admin")
                        .requestMatchers(HttpMethod.DELETE, "/api/doctors/*").hasAnyAuthority("delete-doctor", "admin", "super-admin", "role_admin", "role_super_admin")

                        .requestMatchers(HttpMethod.GET, "/api/appointments/doctor/*/slots").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/appointments/my").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/appointments/*").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/appointments").hasAnyAuthority("admin", "super-admin", "role_admin", "role_super_admin")
                        .requestMatchers(HttpMethod.POST, "/api/appointments").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/appointments/*").hasAnyAuthority("user", "doctor", "admin", "super-admin", "role_user", "role_doctor", "role_admin", "role_super_admin")
                        .requestMatchers(HttpMethod.PATCH, "/api/appointments/*/status").hasAnyAuthority("doctor", "admin", "super-admin", "role_doctor", "role_admin", "role_super_admin")
                        .requestMatchers(HttpMethod.POST, "/api/appointments/*/cancel").hasAnyAuthority("user", "doctor", "admin", "super-admin", "role_user", "role_doctor", "role_admin", "role_super_admin")
                        .requestMatchers(HttpMethod.DELETE, "/api/appointments/*").hasAnyAuthority("admin", "super-admin", "role_admin", "role_super_admin")

                        .requestMatchers(HttpMethod.POST,"/api/nurses").hasAuthority("create-nurse")
                        .requestMatchers(HttpMethod.PUT, "/api/nurses/*").hasAuthority("update-nurse")
                        .requestMatchers(HttpMethod.DELETE, "/api/nurses/*").hasAuthority("delete-nurse")

                        .requestMatchers(HttpMethod.GET, "/api/profiles/self").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/profiles/self").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/profiles/*").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/profiles").hasAnyAuthority("admin", "super-admin", "role_admin", "role_super_admin")
                        .requestMatchers(HttpMethod.POST, "/api/profiles/**").hasAnyAuthority("create-profile", "admin", "super-admin", "role_admin", "role_super_admin")
                        .requestMatchers(HttpMethod.PUT, "/api/profiles/**").hasAnyAuthority("update-profile", "admin", "super-admin", "role_admin", "role_super_admin")
                        .requestMatchers(HttpMethod.DELETE, "/api/profiles/**").hasAnyAuthority("delete-profile", "admin", "super-admin", "role_admin", "role_super_admin")

                        .requestMatchers(HttpMethod.POST, "/api/hospitals/*/medical-tests").hasAuthority("create-hospital-medical-test")
                        .requestMatchers(HttpMethod.PUT, "/api/hospitals/*/medical-tests/*").hasAuthority("update-hospital-medical-test")
                        .requestMatchers(HttpMethod.DELETE, "/api/hospitals/*/medical-tests/*").hasAuthority("delete-hospital-medical-test")

                        .requestMatchers(HttpMethod.POST, "/api/hospitals/*/amenities").hasAuthority("create-hospital-amenity")
                        .requestMatchers(HttpMethod.PUT, "/api/hospitals/*/amenities/*").hasAuthority("update-hospital-amenity")
                        .requestMatchers(HttpMethod.DELETE, "/api/hospitals/*/amenities/*").hasAuthority("delete-hospital-amenity")

                        .requestMatchers(HttpMethod.POST, "/api/hospitals").hasAnyAuthority("create-hospital", "admin", "super-admin", "role_admin", "role_super_admin")
                        .requestMatchers(HttpMethod.PUT, "/api/hospitals/*").hasAnyAuthority("update-hospital", "admin", "super-admin", "role_admin", "role_super_admin")
                        .requestMatchers(HttpMethod.DELETE, "/api/hospitals/*").hasAnyAuthority("delete-hospital", "admin", "super-admin", "role_admin", "role_super_admin")

                        .requestMatchers(HttpMethod.POST, "/api/social-organization").hasAuthority("create-social-organization")
                        .requestMatchers(HttpMethod.PUT, "/api/social-organization/*").hasAuthority("update-social-organization")
                        .requestMatchers(HttpMethod.DELETE, "/api/social-organization/*").hasAuthority("delete-social-organization")

                        .requestMatchers(HttpMethod.POST, "/api/institutions").hasAuthority("create-institution")
                        .requestMatchers(HttpMethod.PUT, "/api/institutions/*").hasAuthority("update-institution")
                        .requestMatchers(HttpMethod.DELETE, "/api/institutions/*").hasAuthority("delete-institution")

                        .requestMatchers(HttpMethod.POST, "/api/associations").hasAuthority("create-master-data")
                        .requestMatchers(HttpMethod.PUT, "/api/associations/*").hasAuthority("update-master-data")
                        .requestMatchers(HttpMethod.DELETE, "/api/associations/*").hasAuthority("delete-master-data")

                        .requestMatchers(HttpMethod.POST, "/api/medical-tests").hasAuthority("create-master-data")
                        .requestMatchers(HttpMethod.PUT, "/api/medical-tests/*").hasAuthority("update-master-data")
                        .requestMatchers(HttpMethod.DELETE, "/api/medical-tests/*").hasAuthority("delete-master-data")

                        .requestMatchers(HttpMethod.POST, "/api/ambulances").hasAuthority("create-ambulance")
                        .requestMatchers(HttpMethod.PUT, "/api/ambulances/*").hasAuthority("update-ambulance")
                        .requestMatchers(HttpMethod.DELETE, "/api/ambulances/*").hasAuthority("delete-ambulance")

                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/actuator/health/liveness").permitAll()
                        .requestMatchers("/actuator/health/readiness").permitAll()
                        .requestMatchers("/actuator/**").hasAuthority("actuator")

                        .requestMatchers("/api/health-vitals/**").authenticated()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
                        .accessDeniedHandler(new BearerTokenAccessDeniedHandler())
                )
                .addFilterBefore(coreScopeApiFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    @Primary
    public JwtDecoder jwtDecoder() {
        return JwtDecoders.fromIssuerLocation(issuerUri);
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        // Converts JWT claims into Spring authorities used by hasAuthority/hasAnyAuthority checks.
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            List<String> roleNames = extractRolesFromToken(jwt);
            if (!roleNames.isEmpty()) {
                return roleNames.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
            }

            String userId = jwt.getClaimAsString("sub");
            if (userId != null) {
                try {
                    // Fallback: fetch roles from Keycloak when token lacks role claims
                    List<String> keycloakRoles = keycloakService.getUserRealmRoleNames(userId).block();
                    if (keycloakRoles != null) {
                        return keycloakRoles.stream()
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList());
                    }
                } catch (Exception e) {
                    log.error("Failed to fetch roles from Keycloak for user: {}", userId, e);
                }
            }

            return Collections.emptyList();
        });
        return jwtAuthenticationConverter;
    }

    private List<String> extractRolesFromToken(org.springframework.security.oauth2.jwt.Jwt jwt) {
        // Keycloak stores realm roles in realm_access.roles.
        Object realmAccess = jwt.getClaim("realm_access");
        if (realmAccess instanceof java.util.Map<?, ?> realmAccessMap) {
            Object roles = realmAccessMap.get("roles");
            if (roles instanceof java.util.Collection<?> roleCollection) {
                return roleCollection.stream()
                        .filter(role -> role instanceof String)
                        .map(role -> (String) role)
                        .toList();
            }
        }
        return Collections.emptyList();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // CORS policy used by Spring Security chain.
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("*"));
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowedMethods(List.of("OPTIONS", "HEAD", "GET", "PUT", "POST", "DELETE", "PATCH"));
        config.setAllowCredentials(false);
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public CorsFilter corsFilter() {
        // Additional CORS filter bean for compatibility with existing filter stack.
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("*"));
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowedMethods(List.of("OPTIONS", "HEAD", "GET", "PUT", "POST", "DELETE", "PATCH"));
        config.setAllowCredentials(false);
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // Paths here are bypassed by Spring Security entirely (no auth filter execution).
        return web -> web.ignoring()
                .requestMatchers(
                        "/api-docs/**",
                        "configuration/**",
                        "/swagger*/**",
                        "/webjars/**",
                        "/swagger-ui/**"
                )
                .requestMatchers(HttpMethod.GET,
                        "/api/age-groups/**",
                        "/api/ambulance-types/**",
                        "/api/association-types/**",
                        "/api/blood-components/**",
                        "/api/blood-donation-badges/**",
                        "/api/blood-groups/**",
                        "/api/campaign-types/**",
                        "/api/contribution-actions/**",
                        "/api/contribution-badges/**",
                        "/api/countries/**",
                        "/api/currency-types/**",
                        "/api/days-of-week/**",
                        "/api/degree-types/**",
                        "/api/doctor-badges/**",
                        "/api/document-types/**",
                        "/api/domain/**",
                        "/api/equipment-categories/**",
                        "/api/gender/**",
                        "/api/hospital-amenity-types/**",
                        "/api/hospital-types/**",
                        "/api/institution-types/**",
                        "/api/membership-types/**",
                        "/api/minio-directories/**",
                        "/api/organization-types/**",
                        "/api/permissions/**",
                        "/api/positions/**",
                        "/api/requisition-statuses/**",
                        "/api/social-organization-types/**",
                        "/api/teacher-positions/**",
                        "/api/user-types/**")
                .requestMatchers(HttpMethod.GET,
                        "/api/districts/**",
                        "/api/divisions/**",
                        "/api/upazilas/**")
                .requestMatchers(HttpMethod.GET,
                        "/api/home/**",
                        "/api/medical-specialities/**",
                        "/api/degrees/**",
                        "/api/medical-tests/**",
                        "/api/institutions/**",
                        "/api/hospitals/**",
                        "/api/doctors/**",
                        "/api/nurses/**",
                        "/api/hospital-medical-tests/**",
                        "/api/ambulances/**",
                        "/api/associations/**",
                        "/api/medicines/**",
                        "/api/social-organization/**",
                        "/api/blood-requisitions/**",
                        "/api/blood-donations/**",
                        "/api/blood-donors/**",
                        "/api/superadmin/**",
                        "/api/hospital-amenity/**");
    }
}
