package com.ciphertext.opencarebackend.modules.auth.model;

/**
 * Flow note: AuthRoleType belongs to the authentication module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public enum AuthRoleType {
    ADMIN,
    DOCTOR,
    PATIENT;

    public static AuthRoleType fromInput(String value) {
        if (value == null || value.isBlank()) {
            return PATIENT;
        }
        return AuthRoleType.valueOf(value.trim().toUpperCase());
    }
}