package com.ecommerce.userservice.validation;

import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class UserInputNormalizer {

    public String requiredText(String value) {
        return value.trim();
    }

    public String username(String value) {
        return requiredText(value).toLowerCase(Locale.ROOT);
    }

    public String email(String value) {
        return requiredText(value).toLowerCase(Locale.ROOT);
    }

    public String phone(String value) {
        String normalized = optionalText(value);
        if (normalized == null || normalized.startsWith("+84")) {
            return normalized;
        }
        return "+84" + normalized.substring(1);
    }

    public String optionalText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    public String keyword(String value) {
        String normalized = optionalText(value);
        return normalized == null ? "" : normalized.toLowerCase(Locale.ROOT);
    }
}
