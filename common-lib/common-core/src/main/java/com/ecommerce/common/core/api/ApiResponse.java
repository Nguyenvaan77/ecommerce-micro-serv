package com.ecommerce.common.core.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        T data,
        String message,
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        Instant timestamp
) {

    public ApiResponse {
        if (!success) {
            throw new IllegalArgumentException("ApiResponse represents successful responses only");
        }
        timestamp = Objects.requireNonNull(timestamp, "timestamp must not be null");
    }

    public static <T> ApiResponse<T> success(T data) {
        return success(data, null);
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, data, message, Instant.now());
    }
}
