package com.ciphertext.opencarebackend.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.Duration;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class StartupDependencyValidator implements ApplicationRunner {

    private final DataSource dataSource;
    private final RestTemplateBuilder restTemplateBuilder;

    @Value("${app.keycloak.server-url}")
    private String keycloakServerUrl;

    @Value("${app.keycloak.realm}")
    private String keycloakRealm;

    @Override
    public void run(ApplicationArguments args) {
        validateDatabase();
        validateKeycloak();
        log.info("Startup dependency validation passed: PostgreSQL and Keycloak are reachable.");
    }

    private void validateDatabase() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT 1")) {

            if (!connection.isValid(5)) {
                throw new IllegalStateException("Database connection is not valid");
            }
            statement.execute();
        } catch (Exception ex) {
            throw new IllegalStateException(
                    "Startup dependency check failed: PostgreSQL is unreachable or misconfigured. "
                            + "Verify DB_HOSTNAME, DB_PORT, POSTGRES_USERNAME, and POSTGRES_PASSWORD.",
                    ex
            );
        }
    }

    private void validateKeycloak() {
        String normalizedServerUrl = keycloakServerUrl.endsWith("/")
                ? keycloakServerUrl.substring(0, keycloakServerUrl.length() - 1)
                : keycloakServerUrl;

        String discoveryUrl = normalizedServerUrl + "/realms/" + keycloakRealm + "/.well-known/openid-configuration";

        RestTemplate restTemplate = restTemplateBuilder
                .connectTimeout(Duration.ofSeconds(5))
                .readTimeout(Duration.ofSeconds(5))
                .build();

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    discoveryUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new IllegalStateException("Keycloak discovery response was empty or non-2xx");
            }
        } catch (RestClientException ex) {
            throw new IllegalStateException(
                    "Startup dependency check failed: Keycloak is unreachable at " + discoveryUrl + ". "
                            + "Verify KEYCLOAK_SERVER_URL and KEYCLOAK_REALM.",
                    ex
            );
        }
    }
}