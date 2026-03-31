package com.ciphertext.opencarebackend.modules.provider.dto.response.enums;

public record DoctorBadgeResponse(String value, int minDonations, int maxDonations,
                                  String icon, String levelName, String description) {
}