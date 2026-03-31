package com.ciphertext.opencarebackend.modules.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class RegistrationResponse {
    private String message;
    private String userId;
    private String status;
}
