package com.ecommerce.common.core.error;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class CommonErrorCodeTest {

    @Test
    void exposesUniqueAndCompleteErrorDefinitions() {
        assertThat(Arrays.stream(CommonErrorCode.values()).map(CommonErrorCode::code))
                .doesNotHaveDuplicates();

        for (CommonErrorCode errorCode : CommonErrorCode.values()) {
            assertThat(errorCode.code()).isNotBlank();
            assertThat(errorCode.messageKey()).isNotBlank();
            assertThat(errorCode.defaultMessage()).isNotBlank();
            assertThat(errorCode.httpStatus().isError()).isTrue();
        }
    }

    @Test
    void mapsTechnicalErrorsToExpectedStatuses() {
        assertThat(CommonErrorCode.VALIDATION_ERROR.httpStatus().value()).isEqualTo(400);
        assertThat(CommonErrorCode.BAD_REQUEST.httpStatus().value()).isEqualTo(400);
        assertThat(CommonErrorCode.RESOURCE_NOT_FOUND.httpStatus().value()).isEqualTo(404);
        assertThat(CommonErrorCode.UNAUTHORIZED.httpStatus().value()).isEqualTo(401);
        assertThat(CommonErrorCode.FORBIDDEN.httpStatus().value()).isEqualTo(403);
        assertThat(CommonErrorCode.INTERNAL_SERVER_ERROR.httpStatus().value()).isEqualTo(500);
    }
}
