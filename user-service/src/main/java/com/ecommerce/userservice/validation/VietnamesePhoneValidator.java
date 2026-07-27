package com.ecommerce.userservice.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public final class VietnamesePhoneValidator
        implements ConstraintValidator<VietnamesePhone, String> {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^(?:0|\\+84)\\d{9}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null
                || value.isBlank()
                || PHONE_PATTERN.matcher(value.trim()).matches();
    }
}
