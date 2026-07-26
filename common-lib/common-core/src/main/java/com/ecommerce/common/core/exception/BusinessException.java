package com.ecommerce.common.core.exception;

import com.ecommerce.common.core.error.ErrorCode;

import java.util.Arrays;
import java.util.Objects;

public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Object[] messageArgs;

    public BusinessException(ErrorCode errorCode, Object... messageArgs) {
        super(Objects.requireNonNull(errorCode, "errorCode must not be null").defaultMessage());
        this.errorCode = errorCode;
        this.messageArgs = messageArgs == null ? new Object[0] : Arrays.copyOf(messageArgs, messageArgs.length);
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public Object[] getMessageArgs() {
        return Arrays.copyOf(messageArgs, messageArgs.length);
    }
}
