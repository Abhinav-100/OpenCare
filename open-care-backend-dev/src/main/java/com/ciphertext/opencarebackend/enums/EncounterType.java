package com.ciphertext.opencarebackend.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum EncounterType {
    OPD("Outpatient Visit"),
    IPD("Inpatient Visit"),
    EMERGENCY("Emergency"),
    TELECONSULT("Teleconsultation"),
    LAB_VISIT("Laboratory Visit"),
    FOLLOW_UP("Follow-up Visit");

    private final String displayName;

    EncounterType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getValue() {
        return this.name();
    }
}