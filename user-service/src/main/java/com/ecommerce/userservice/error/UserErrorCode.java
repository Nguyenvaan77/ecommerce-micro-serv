package com.ecommerce.userservice.error;

import com.ecommerce.common.core.error.ErrorCode;
import org.springframework.http.HttpStatus;

public enum UserErrorCode implements ErrorCode {

    USER_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "user.error.not-found",
            "User with id {0} was not found"
    ),
    USERNAME_ALREADY_EXISTS(
            HttpStatus.CONFLICT,
            "user.error.username-already-exists",
            "Username already exists"
    ),
    EMAIL_ALREADY_EXISTS(
            HttpStatus.CONFLICT,
            "user.error.email-already-exists",
            "Email already exists"
    ),
    PHONE_ALREADY_EXISTS(
            HttpStatus.CONFLICT,
            "user.error.phone-already-exists",
            "Phone number already exists"
    ),
    USER_UNIQUE_CONFLICT(
            HttpStatus.CONFLICT,
            "user.error.unique-conflict",
            "User data conflicts with an existing user"
    ),
    USER_PAGE_OUT_OF_RANGE(
            HttpStatus.BAD_REQUEST,
            "user.error.page-out-of-range",
            "Requested page {0} is out of range"
    ),
    ROLE_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "user.error.role-not-found",
            "Role {0} was not found"
    );

    private final HttpStatus httpStatus;
    private final String messageKey;
    private final String defaultMessage;

    UserErrorCode(HttpStatus httpStatus, String messageKey, String defaultMessage) {
        this.httpStatus = httpStatus;
        this.messageKey = messageKey;
        this.defaultMessage = defaultMessage;
    }

    @Override
    public String code() {
        return name();
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
