package com.ciphertext.opencarebackend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = EnumNamePatternValidator.class)
@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
public @interface EnumNamePattern {
    String message() default "Invalid enum value";

    Class<? extends Enum<?>> enumClass();

    boolean ignoreCase() default false;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}