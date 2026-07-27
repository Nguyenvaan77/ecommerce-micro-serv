package com.ecommerce.common.core.api;

import java.util.Objects;

public record FieldError(
        String field,
        String message
) {

    public FieldError {
        field = Objects.requireNonNull(field, "field must not be null");
        message = Objects.requireNonNull(message, "message must not be null");
        if (field.isBlank()) {
            throw new IllegalArgumentException("field must not be blank");
        }
        if (message.isBlank()) {
            throw new IllegalArgumentException("message must not be blank");
        }
    }
}
