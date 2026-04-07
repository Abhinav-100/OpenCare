package com.ciphertext.opencarebackend.modules.provider.dto.response.enums;

/**
 * Flow note: DoctorBadgeResponse belongs to the provider doctor/hospital module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public record DoctorBadgeResponse(String value, int minDonations, int maxDonations,
                                  String icon, String levelName, String description) {
}