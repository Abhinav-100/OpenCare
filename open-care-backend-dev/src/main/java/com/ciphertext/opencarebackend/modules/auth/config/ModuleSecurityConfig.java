package com.ciphertext.opencarebackend.modules.auth.config;

import com.ciphertext.opencarebackend.modules.auth.security.ModuleJwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class ModuleSecurityConfig {

    private final ModuleJwtAuthenticationFilter moduleJwtAuthenticationFilter;

    @Bean
    @Order(1)
    public SecurityFilterChain moduleSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/modules/**")
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/modules/auth/login", "/api/modules/auth/register", "/api/modules/auth/overview", "/api/modules/patient/register").permitAll()
                        .requestMatchers("/api/modules/billing/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/modules/billing/bills/generate").hasAnyRole("DOCTOR", "ADMIN")
                        .requestMatchers("/api/modules/billing/payments/record").hasAnyRole("PATIENT", "DOCTOR", "ADMIN")
                        .requestMatchers("/api/modules/billing/bills/*/payments").hasAnyRole("PATIENT", "DOCTOR", "ADMIN")
                        .requestMatchers("/api/modules/appointments/**").hasAnyRole("PATIENT", "DOCTOR", "ADMIN")
                        .requestMatchers("/api/modules/doctor/**").hasAnyRole("DOCTOR", "ADMIN")
                        .requestMatchers("/api/modules/patient/**").hasAnyRole("PATIENT", "DOCTOR", "ADMIN")
                        .requestMatchers("/api/modules/lab/**").hasAnyRole("DOCTOR", "ADMIN")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .addFilterBefore(moduleJwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}