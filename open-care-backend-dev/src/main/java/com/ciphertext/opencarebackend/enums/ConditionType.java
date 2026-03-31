package com.ciphertext.opencarebackend.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ConditionType {
    ALLERGY("Allergy"),
    DIAGNOSIS("Diagnosis"),
    CHRONIC("Chronic Condition");

    private final String displayName;

    ConditionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getValue() {
        return this.name();
    }
}