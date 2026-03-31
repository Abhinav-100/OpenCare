package com.ciphertext.opencarebackend.enums;
import com.ciphertext.opencarebackend.modules.blood.dto.response.enums.RequisitionStatusResponse;
public enum RequisitionStatus {
    ACTIVE("Active"),
    FULFILLED("Fulfilled"),
    EXPIRED("Expired"),
    CANCELLED("Cancelled");

    private final String name;

    RequisitionStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    public RequisitionStatusResponse toResponse() {
        return new RequisitionStatusResponse(this.name(), this.name);
    }
}
