package com.ciphertext.opencarebackend.modules.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
/**
 * Flow note: RegistrationResponse belongs to the authentication module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public class RegistrationResponse {
    private String message;
    private String userId;
    private String status;
}
