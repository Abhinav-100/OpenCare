package com.ciphertext.opencarebackend.enums;
import com.ciphertext.opencarebackend.modules.appointment.dto.response.enums.AppointmentStatusResponse;
import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum AppointmentStatus {
    PENDING("Pending Confirmation"),
    CONFIRMED("Confirmed"),
    CANCELLED("Cancelled"),
    COMPLETED("Completed"),
    NO_SHOW("No Show");

    private final String description;

    AppointmentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public AppointmentStatusResponse toResponse() {
        return new AppointmentStatusResponse(this.name(), description);
    }
}
