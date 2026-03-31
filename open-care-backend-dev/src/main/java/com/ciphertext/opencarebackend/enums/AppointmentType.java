package com.ciphertext.opencarebackend.enums;
import com.ciphertext.opencarebackend.modules.appointment.dto.response.enums.AppointmentTypeResponse;
import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum AppointmentType {
    ONLINE("Online Consultation"),
    OFFLINE("In-Person Visit");

    private final String description;

    AppointmentType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public AppointmentTypeResponse toResponse() {
        return new AppointmentTypeResponse(this.name(), description);
    }
}
