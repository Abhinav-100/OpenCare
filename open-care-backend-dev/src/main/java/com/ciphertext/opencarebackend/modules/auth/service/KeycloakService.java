package com.ciphertext.opencarebackend.modules.auth.service;
import com.ciphertext.opencarebackend.modules.auth.dto.request.KeycloakRegistrationRequest;
import com.ciphertext.opencarebackend.modules.auth.dto.response.TokenResponse;
import com.ciphertext.opencarebackend.exception.KeycloakClientException;
import com.ciphertext.opencarebackend.exception.KeycloakServerException;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

@Service
public class KeycloakService {

    private final WebClient webClient;
    private final String serverUrl;
    private final String realm;
    private final String clientId;
    private final String clientSecret;
    private final String adminClientId;
    private final String adminRealm;
    private final String adminUsername;
    private final String adminPassword;
    private final String redirectUri;

    public KeycloakService(
            WebClient.Builder webClientBuilder,
            @Value("${app.keycloak.server-url}") String serverUrl,
            @Value("${app.keycloak.realm}") String realm,
            @Value("${app.keycloak.client-id}") String clientId,
            @Value("${app.keycloak.client-secret}") String clientSecret,
            @Value("${app.keycloak.admin.client-id}") String adminClientId,
            @Value("${app.keycloak.admin.realm}") String adminRealm,
            @Value("${app.keycloak.admin.username}") String adminUsername,
            @Value("${app.keycloak.admin.password}") String adminPassword,
            @Value("${app.frontend.redirect-uri}") String redirectUri
    ) {
        this.webClient = webClientBuilder.build();
        this.serverUrl = serverUrl;
        this.realm = realm;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.adminClientId = adminClientId;
        this.adminRealm = adminRealm;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
        this.redirectUri = redirectUri;
    }

    public Mono<TokenResponse> login(String username, String password) {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            return Mono.error(new KeycloakClientException("Username and password are required"));
        }
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "password");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("username", username);
        formData.add("password", password);

        return executeTokenRequest(formData);
    }

    public Mono<TokenResponse> refreshToken(String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            return Mono.error(new KeycloakClientException("Refresh token is required"));
        }
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "refresh_token");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("refresh_token", refreshToken);

        return executeTokenRequest(formData);
    }

    public Mono<Void> logout(String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            return Mono.error(new KeycloakClientException("Refresh token is required"));
        }
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("refresh_token", refreshToken);

        String logoutUrl = serverUrl + "/realms/" + realm + "/protocol/openid-connect/logout";

        return executeRequest(logoutUrl, formData, Void.class);
    }

    public Mono<String> registerUser(KeycloakRegistrationRequest registrationRequest, String groupName) {
        return getAdminToken().flatMap(adminToken -> {
            String usersUrl = serverUrl + "/admin/realms/" + realm + "/users";

            return webClient.post()
                    .uri(usersUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                    .body(BodyInserters.fromValue(registrationRequest))
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, this::handleClientError)
                    .onStatus(HttpStatusCode::is5xxServerError, this::handleServerError)
                    .toBodilessEntity()
                    .flatMap(response -> {
                        URI location = response.getHeaders().getLocation();
                        if (location != null) {
                            String userId = location.getPath().substring(location.getPath().lastIndexOf("/") + 1);
                            if (StringUtils.hasText(groupName)) {
                                return addUserToGroup(userId, groupName, adminToken)
                                        .then(Mono.just(userId));
                            }
                            return Mono.just(userId);
                        }
                        return Mono.error(new KeycloakClientException("User created but no Location header returned"));
                    });
        });
    }

    public Mono<Void> addUserToGroup(String userId, String groupName, String adminToken) {
        if (!StringUtils.hasText(userId) || !StringUtils.hasText(groupName)) {
            return Mono.error(new KeycloakClientException("User ID and group name are required"));
        }
        return getGroupId(groupName, adminToken)
                .flatMap(groupId -> {
                    String addToGroupUrl = serverUrl + "/admin/realms/" + realm + "/users/" + userId + "/groups/" + groupId;

                    return webClient.put()
                            .uri(addToGroupUrl)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                            .retrieve()
                            .onStatus(HttpStatusCode::is4xxClientError, this::handleClientError)
                            .onStatus(HttpStatusCode::is5xxServerError, this::handleServerError)
                            .toBodilessEntity()
                            .then();
                });
    }

    private Mono<String> getGroupId(String groupName, String adminToken) {
        if (!StringUtils.hasText(groupName)) {
            return Mono.error(new KeycloakClientException("Group name is required"));
        }
        String groupsUrl = serverUrl + "/admin/realms/" + realm + "/groups?search=" + groupName;

        return webClient.get()
                .uri(groupsUrl)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, this::handleClientError)
                .onStatus(HttpStatusCode::is5xxServerError, this::handleServerError)
                .bodyToFlux(GroupRepresentation.class)
                .filter(group -> groupName.equals(group.getName()))
                .next()
                .map(GroupRepresentation::getId)
                .switchIfEmpty(Mono.error(new KeycloakClientException("Group not found: " + groupName)));
    }

    public Mono<Void> resetPassword(String email) {
        if (!StringUtils.hasText(email)) {
            return Mono.error(new KeycloakClientException("Email is required"));
        }
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", clientId);
        formData.add("email", email);

        String resetUrl = serverUrl + "/realms/" + realm + "/login-actions/reset-credentials";

        return executeRequest(resetUrl, formData, Void.class);
    }

    private Mono<String> getAdminToken() {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", adminClientId);
        formData.add("username", adminUsername);
        formData.add("password", adminPassword);
        formData.add("grant_type", "password");

        String tokenUrl = serverUrl + "/realms/" + adminRealm + "/protocol/openid-connect/token";

        return executeRequest(tokenUrl, formData, TokenResponse.class)
                .map(TokenResponse::getAccess_token);
    }

    public Mono<TokenResponse> exchangeCodeForToken(String code) {
        if (!StringUtils.hasText(code)) {
            return Mono.error(new KeycloakClientException("Authorization code is required"));
        }
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("code", code);
        formData.add("redirect_uri", redirectUri);

        return executeTokenRequest(formData);
    }

    private Mono<TokenResponse> executeTokenRequest(MultiValueMap<String, String> formData) {
        String tokenUrl = serverUrl + "/realms/" + realm + "/protocol/openid-connect/token";
        return executeRequest(tokenUrl, formData, TokenResponse.class);
    }

    private <T> Mono<T> executeRequest(String url, MultiValueMap<String, String> formData, Class<T> responseType) {
        return webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, this::handleClientError)
                .onStatus(HttpStatusCode::is5xxServerError, this::handleServerError)
                .bodyToMono(responseType);
    }

    public Mono<List<RoleRepresentation>> getUserRealmRoles(String userId) {
        if (!StringUtils.hasText(userId)) {
            return Mono.error(new KeycloakClientException("User ID is required"));
        }
        return getAdminToken().flatMap(adminToken -> {
            String userRolesUrl = serverUrl + "/admin/realms/" + realm + "/users/" + userId + "/role-mappings/realm/composite";

            return webClient.get()
                    .uri(userRolesUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, this::handleClientError)
                    .onStatus(HttpStatusCode::is5xxServerError, this::handleServerError)
                    .bodyToFlux(RoleRepresentation.class)
                    .collectList();
        });
    }

    public Mono<List<String>> getUserRealmRoleNames(String userId) {
        return getUserRealmRoles(userId)
                .map(roles -> roles.stream()
                        .map(RoleRepresentation::getName)
                        .collect(java.util.stream.Collectors.toList()));
    }

    private Mono<Throwable> handleClientError(ClientResponse response) {
        return response.bodyToMono(String.class)
                .flatMap(body -> Mono.error(new KeycloakClientException("Keycloak client error: " + response.statusCode() + " " + body)));
    }

    private Mono<Throwable> handleServerError(ClientResponse response) {
        return response.bodyToMono(String.class)
                .flatMap(body -> Mono.error(new KeycloakServerException("Keycloak server error: " + response.statusCode() + " " + body)));
    }
}
