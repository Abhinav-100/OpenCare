package com.ciphertext.opencarebackend.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class EnumNamePatternValidator implements ConstraintValidator<EnumNamePattern, String> {

    private Set<String> allowedValues;
    private boolean ignoreCase;

    @Override
    public void initialize(EnumNamePattern annotation) {
        ignoreCase = annotation.ignoreCase();
        allowedValues = Arrays.stream(annotation.enumClass().getEnumConstants())
            .map(Enum::name)
            .map(name -> ignoreCase ? name.toLowerCase() : name)
            .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }
        String candidate = ignoreCase ? value.toLowerCase() : value;
        return allowedValues.contains(candidate);
    }
}