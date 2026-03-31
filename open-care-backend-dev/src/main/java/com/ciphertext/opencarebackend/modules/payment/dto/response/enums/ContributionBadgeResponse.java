package com.ciphertext.opencarebackend.modules.payment.dto.response.enums;

public record ContributionBadgeResponse(String value, int minDonations, int maxDonations,
                                        String icon, String levelName, String description) {
}