package com.ecommerce.userservice.error;

import com.ecommerce.common.core.api.ApiError;
import com.ecommerce.common.core.api.FieldError;
import com.ecommerce.common.core.error.CommonErrorCode;
import com.ecommerce.common.core.i18n.CommonMessageResolver;
import com.ecommerce.userservice.controller.LocalUserRoleController;
import com.ecommerce.userservice.controller.UserController;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.List;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(assignableTypes = {
        UserController.class,
        LocalUserRoleController.class
})
public class UserWebExceptionHandler {

    private final CommonMessageResolver messageResolver;

    public UserWebExceptionHandler(CommonMessageResolver messageResolver) {
        this.messageResolver = messageResolver;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(
            MethodArgumentTypeMismatchException exception,
            HttpServletRequest request
    ) {
        CommonErrorCode errorCode = CommonErrorCode.BAD_REQUEST;
        String message = messageResolver.resolve(
                errorCode,
                LocaleContextHolder.getLocale()
        );
        ApiError body = new ApiError(
                errorCode.code(),
                message,
                errorCode.httpStatus().value(),
                request.getRequestURI(),
                Instant.now(),
                List.of(new FieldError(exception.getName(), "Invalid value"))
        );
        return ResponseEntity.status(errorCode.httpStatus()).body(body);
    }
}
