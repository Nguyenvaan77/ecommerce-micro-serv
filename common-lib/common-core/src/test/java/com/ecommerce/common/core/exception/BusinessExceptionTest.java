package com.ecommerce.common.core.exception;

import com.ecommerce.common.core.error.CommonErrorCode;
import com.ecommerce.common.core.error.ErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BusinessExceptionTest {

    @Test
    void keepsErrorCodeAndDefensivelyCopiesMessageArguments() {
        Object[] arguments = {"product-1"};
        BusinessException exception =
                new BusinessException(CommonErrorCode.BAD_REQUEST, arguments);
        arguments[0] = "changed";

        assertThat(exception.getErrorCode()).isEqualTo(CommonErrorCode.BAD_REQUEST);
        assertThat(exception.getMessageArgs()).containsExactly("product-1");

        Object[] returnedArguments = exception.getMessageArgs();
        returnedArguments[0] = "changed-again";
        assertThat(exception.getMessageArgs()).containsExactly("product-1");
    }

    @Test
    void supportsDefaultAndDomainSpecificNotFoundCodes() {
        assertThat(new NotFoundException().getErrorCode())
                .isEqualTo(CommonErrorCode.RESOURCE_NOT_FOUND);

        ErrorCode productNotFound = new TestErrorCode(HttpStatus.NOT_FOUND);
        assertThat(new NotFoundException(productNotFound, "product-1").getErrorCode())
                .isSameAs(productNotFound);
    }

    @Test
    void rejectsNonNotFoundErrorCode() {
        assertThatThrownBy(() -> new NotFoundException(CommonErrorCode.BAD_REQUEST))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("404");
    }

    private record TestErrorCode(HttpStatus httpStatus) implements ErrorCode {

        @Override
        public String code() {
            return "PRODUCT_NOT_FOUND";
        }

        @Override
        public String messageKey() {
            return "product.not-found";
        }

        @Override
        public String defaultMessage() {
            return "Product not found";
        }
    }
}
