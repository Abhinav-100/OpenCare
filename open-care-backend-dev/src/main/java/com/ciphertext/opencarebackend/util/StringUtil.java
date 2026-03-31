package com.ciphertext.opencarebackend.util;

import java.security.SecureRandom;

public final class StringUtil {

    private static final String ALPHANUMERIC_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private StringUtil() {
        // Utility class
    }

    public static String generateRandomPassword(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Password length must be positive");
        }

        StringBuilder password = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = SECURE_RANDOM.nextInt(ALPHANUMERIC_CHARACTERS.length());
            password.append(ALPHANUMERIC_CHARACTERS.charAt(index));
        }
        return password.toString();
    }
}