package com.ciphertext.opencarebackend.modules.provider.dto.response.enums;

/**
 * Flow note: HospitalTypeResponse belongs to the provider doctor/hospital module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public record HospitalTypeResponse(
        String value,
        String banglaName,
        String englishName
) {}