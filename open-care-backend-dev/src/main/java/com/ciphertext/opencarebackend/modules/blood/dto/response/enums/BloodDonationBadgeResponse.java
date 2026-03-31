package com.ciphertext.opencarebackend.modules.blood.dto.response.enums;

public record BloodDonationBadgeResponse(String value, int minDonations, int maxDonations,
                                         String icon, String levelName, String description) {
}