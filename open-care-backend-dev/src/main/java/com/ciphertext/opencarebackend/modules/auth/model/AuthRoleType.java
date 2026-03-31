package com.ciphertext.opencarebackend.modules.auth.model;

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