package com.ecommerce.common.core.exception;

import com.ecommerce.common.core.error.CommonErrorCode;
import com.ecommerce.common.core.error.ErrorCode;
import org.springframework.http.HttpStatus;

import java.util.Objects;

public final class NotFoundException extends BusinessException {

    public NotFoundException(Object... messageArgs) {
        super(CommonErrorCode.RESOURCE_NOT_FOUND, messageArgs);
    }

    public NotFoundException(ErrorCode errorCode, Object... messageArgs) {
        super(requireNotFoundStatus(errorCode), messageArgs);
    }

    private static ErrorCode requireNotFoundStatus(ErrorCode errorCode) {
        ErrorCode value = Objects.requireNonNull(errorCode, "errorCode must not be null");
        if (value.httpStatus() != HttpStatus.NOT_FOUND) {
            throw new IllegalArgumentException("NotFoundException requires a 404 error code");
        }
        return value;
    }
}
