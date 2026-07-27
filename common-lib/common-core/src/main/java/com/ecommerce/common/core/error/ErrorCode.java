package com.ecommerce.common.core.error;

import org.springframework.http.HttpStatus;

public interface ErrorCode {

    String code();

    HttpStatus httpStatus();

    String messageKey();

    String defaultMessage();
}
