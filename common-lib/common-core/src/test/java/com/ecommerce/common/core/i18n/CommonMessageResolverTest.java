package com.ecommerce.common.core.i18n;

import com.ecommerce.common.core.error.CommonErrorCode;
import com.ecommerce.common.core.error.ErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.http.HttpStatus;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class CommonMessageResolverTest {

    @Test
    void resolvesBundledEnglishAndVietnameseMessages() {
        CommonMessageResolver resolver = new CommonMessageResolver(new StaticMessageSource());

        assertThat(resolver.resolve(CommonErrorCode.RESOURCE_NOT_FOUND, Locale.ENGLISH))
                .isEqualTo("The requested resource was not found");
        assertThat(resolver.resolve(CommonErrorCode.RESOURCE_NOT_FOUND, Locale.forLanguageTag("vi-VN")))
                .isEqualTo("Không tìm thấy tài nguyên được yêu cầu");
    }

    @Test
    void letsApplicationOverrideCommonMessage() {
        StaticMessageSource applicationMessages = new StaticMessageSource();
        applicationMessages.addMessage(
                CommonErrorCode.BAD_REQUEST.messageKey(),
                Locale.ENGLISH,
                "Application-specific bad request"
        );
        CommonMessageResolver resolver = new CommonMessageResolver(applicationMessages);

        assertThat(resolver.resolve(CommonErrorCode.BAD_REQUEST, Locale.ENGLISH))
                .isEqualTo("Application-specific bad request");
    }

    @Test
    void resolvesApplicationSpecificErrorCode() {
        StaticMessageSource applicationMessages = new StaticMessageSource();
        applicationMessages.addMessage(
                "product.not-found",
                Locale.ENGLISH,
                "Product {0} was not found"
        );
        CommonMessageResolver resolver = new CommonMessageResolver(applicationMessages);

        assertThat(resolver.resolve(new ProductNotFoundError(), Locale.ENGLISH, "product-1"))
                .isEqualTo("Product product-1 was not found");
    }

    @Test
    void fallsBackToDefaultMessageForUnknownKeyAndLocale() {
        CommonMessageResolver resolver = new CommonMessageResolver(new StaticMessageSource());

        assertThat(resolver.resolve(new ProductNotFoundError(), Locale.FRENCH, "product-1"))
                .isEqualTo("Product product-1 was not found");
    }

    private record ProductNotFoundError() implements ErrorCode {

        @Override
        public String code() {
            return "PRODUCT_NOT_FOUND";
        }

        @Override
        public HttpStatus httpStatus() {
            return HttpStatus.NOT_FOUND;
        }

        @Override
        public String messageKey() {
            return "product.not-found";
        }

        @Override
        public String defaultMessage() {
            return "Product {0} was not found";
        }
    }
}
