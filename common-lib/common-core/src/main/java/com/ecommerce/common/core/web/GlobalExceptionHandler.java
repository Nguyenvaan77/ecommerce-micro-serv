package com.ecommerce.common.core.web;

import com.ecommerce.common.core.api.ApiError;
import com.ecommerce.common.core.api.FieldError;
import com.ecommerce.common.core.error.CommonErrorCode;
import com.ecommerce.common.core.error.ErrorCode;
import com.ecommerce.common.core.exception.BusinessException;
import com.ecommerce.common.core.i18n.CommonMessageResolver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;
import java.util.List;

@Order(Ordered.LOWEST_PRECEDENCE)
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Log LOGGER = LogFactory.getLog(GlobalExceptionHandler.class);
    private static final String DEFAULT_FIELD_ERROR_MESSAGE = "Invalid value";

    private final CommonMessageResolver messageResolver;

    public GlobalExceptionHandler(CommonMessageResolver messageResolver) {
        this.messageResolver = messageResolver;
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusinessException(
            BusinessException exception,
            WebRequest request
    ) {
        ErrorCode errorCode = exception.getErrorCode();
        String message = messageResolver.resolve(
                errorCode,
                LocaleContextHolder.getLocale(),
                exception.getMessageArgs()
        );
        return response(errorCode, message, request, List.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpectedException(
            Exception exception,
            WebRequest request
    ) {
        LOGGER.error("Unhandled exception while processing request", exception);
        ErrorCode errorCode = CommonErrorCode.INTERNAL_SERVER_ERROR;
        String message = messageResolver.resolve(errorCode, LocaleContextHolder.getLocale());
        return response(errorCode, message, request, List.of());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        List<FieldError> fieldErrors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new FieldError(
                        error.getField(),
                        error.getDefaultMessage() == null
                                ? DEFAULT_FIELD_ERROR_MESSAGE
                                : error.getDefaultMessage()
                ))
                .toList();

        ErrorCode errorCode = CommonErrorCode.VALIDATION_ERROR;
        String message = messageResolver.resolve(errorCode, LocaleContextHolder.getLocale());
        return ResponseEntity
                .status(errorCode.httpStatus())
                .body(toApiError(errorCode, message, request, fieldErrors));
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        ErrorCode errorCode = CommonErrorCode.BAD_REQUEST;
        String message = messageResolver.resolve(errorCode, LocaleContextHolder.getLocale());
        return ResponseEntity
                .status(errorCode.httpStatus())
                .body(toApiError(errorCode, message, request, List.of()));
    }

    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(
            HandlerMethodValidationException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        List<FieldError> fieldErrors = exception.getAllValidationResults()
                .stream()
                .flatMap(result -> result.getResolvableErrors()
                        .stream()
                        .map(error -> new FieldError(
                                parameterName(result.getMethodParameter().getParameterName(),
                                        result.getMethodParameter().getParameterIndex()),
                                error.getDefaultMessage() == null
                                        ? DEFAULT_FIELD_ERROR_MESSAGE
                                        : error.getDefaultMessage()
                        )))
                .toList();

        ErrorCode errorCode = CommonErrorCode.VALIDATION_ERROR;
        String message = messageResolver.resolve(errorCode, LocaleContextHolder.getLocale());
        return ResponseEntity
                .status(errorCode.httpStatus())
                .body(toApiError(errorCode, message, request, fieldErrors));
    }

    private ResponseEntity<ApiError> response(
            ErrorCode errorCode,
            String message,
            WebRequest request,
            List<FieldError> fieldErrors
    ) {
        return ResponseEntity
                .status(errorCode.httpStatus())
                .body(toApiError(errorCode, message, request, fieldErrors));
    }

    private ApiError toApiError(
            ErrorCode errorCode,
            String message,
            WebRequest request,
            List<FieldError> fieldErrors
    ) {
        return new ApiError(
                errorCode.code(),
                message,
                errorCode.httpStatus().value(),
                requestPath(request),
                Instant.now(),
                fieldErrors
        );
    }

    private String requestPath(WebRequest request) {
        if (request instanceof ServletWebRequest servletWebRequest) {
            return servletWebRequest.getRequest().getRequestURI();
        }
        return "";
    }

    private String parameterName(String name, int index) {
        return name == null || name.isBlank() ? "arg" + index : name;
    }
}
