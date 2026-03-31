package com.ciphertext.opencarebackend.config;

public final class RegionDefaults {

    private RegionDefaults() {
    }

    public static final String INDIAN_PHONE_REGEX = "^(\\+91[6-9]\\d{9}|[6-9]\\d{9})$";
    public static final String INDIAN_PHONE_MESSAGE = "Invalid Indian phone number format. Use +91XXXXXXXXXX or 10-digit mobile number.";
}
