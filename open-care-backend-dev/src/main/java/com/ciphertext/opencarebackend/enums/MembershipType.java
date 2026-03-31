package com.ciphertext.opencarebackend.enums;
import com.ciphertext.opencarebackend.modules.shared.dto.response.enums.MembershipTypeResponse;
import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum MembershipType {
    MEMBER("Member"),
    FELLOW("Fellow"),
    PRESIDENT("President"),
    VICE_PRESIDENT("Vice President"),
    SECRETARY("Secretary"),
    TREASURER("Treasurer"),
    EXECUTIVE_MEMBER("Executive Member"),
    HONORARY_MEMBER("Honorary Member"),
    LIFE_MEMBER("Life Member"),
    ADVISOR("Advisor");

    private final String displayName;

    MembershipType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
    public MembershipTypeResponse toResponse() {
        return new MembershipTypeResponse(
                this.name(), // enum constant name
                displayName
        );
    }

}
