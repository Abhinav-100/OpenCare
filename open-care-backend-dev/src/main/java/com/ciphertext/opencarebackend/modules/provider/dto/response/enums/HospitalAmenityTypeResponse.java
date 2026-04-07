package com.ciphertext.opencarebackend.modules.provider.dto.response.enums;

/**
 * Flow note: HospitalAmenityTypeResponse belongs to the provider doctor/hospital module.
 * Read this with neighboring controller/service/repository files to trace request flow.
 */
public record HospitalAmenityTypeResponse(
        String value,
        String displayName,
        String bnName
) {}