package com.ciphertext.opencarebackend.modules.auth.dto.request;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
/**
 * Flow note: KeycloakRegistrationRequest belongs to the authentication module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public class KeycloakRegistrationRequest {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private boolean enabled;
    private List<Credential> credentials;

    @Data
    public static class Credential {
        private String type;
        private String value;
        private boolean temporary;
    }
}