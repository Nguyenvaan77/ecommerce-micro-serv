package com.ecommerce.common.core.api;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public record ApiError(
        String code,
        String message,
        int status,
        String path,
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        Instant timestamp,
        List<FieldError> fieldErrors
) {

    public ApiError {
        code = Objects.requireNonNull(code, "code must not be null");
        message = Objects.requireNonNull(message, "message must not be null");
        path = Objects.requireNonNull(path, "path must not be null");
        timestamp = Objects.requireNonNull(timestamp, "timestamp must not be null");
        fieldErrors = fieldErrors == null ? List.of() : List.copyOf(fieldErrors);
        if (code.isBlank()) {
            throw new IllegalArgumentException("code must not be blank");
        }
        if (message.isBlank()) {
            throw new IllegalArgumentException("message must not be blank");
        }
        if (status < 400 || status > 599) {
            throw new IllegalArgumentException("status must be an HTTP error status");
        }
    }
}
