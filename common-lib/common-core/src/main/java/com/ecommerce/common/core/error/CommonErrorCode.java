package com.ecommerce.common.core.error;

import org.springframework.http.HttpStatus;

public enum CommonErrorCode implements ErrorCode {

    VALIDATION_ERROR(
            "VALIDATION_ERROR",
            HttpStatus.BAD_REQUEST,
            "common.error.validation",
            "Validation failed"
    ),
    BAD_REQUEST(
            "BAD_REQUEST",
            HttpStatus.BAD_REQUEST,
            "common.error.bad-request",
            "The request is invalid"
    ),
    RESOURCE_NOT_FOUND(
            "RESOURCE_NOT_FOUND",
            HttpStatus.NOT_FOUND,
            "common.error.not-found",
            "The requested resource was not found"
    ),
    UNAUTHORIZED(
            "UNAUTHORIZED",
            HttpStatus.UNAUTHORIZED,
            "common.error.unauthorized",
            "Authentication is required"
    ),
    FORBIDDEN(
            "FORBIDDEN",
            HttpStatus.FORBIDDEN,
            "common.error.forbidden",
            "Access is denied"
    ),
    INTERNAL_SERVER_ERROR(
            "INTERNAL_SERVER_ERROR",
            HttpStatus.INTERNAL_SERVER_ERROR,
            "common.error.internal",
            "An unexpected error occurred"
    );

    private final String code;
    private final HttpStatus httpStatus;
    private final String messageKey;
    private final String defaultMessage;

    CommonErrorCode(
            String code,
            HttpStatus httpStatus,
            String messageKey,
            String defaultMessage
    ) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.messageKey = messageKey;
        this.defaultMessage = defaultMessage;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public HttpStatus httpStatus() {
        return httpStatus;
    }

    @Override
    public String messageKey() {
        return messageKey;
    }

    @Override
    public String defaultMessage() {
        return defaultMessage;
    }
}
