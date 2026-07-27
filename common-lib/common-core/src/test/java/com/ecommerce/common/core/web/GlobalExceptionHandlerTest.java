package com.ecommerce.common.core.web;

import com.ecommerce.common.core.error.ErrorCode;
import com.ecommerce.common.core.exception.BusinessException;
import com.ecommerce.common.core.exception.NotFoundException;
import com.ecommerce.common.core.i18n.CommonMessageResolver;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        StaticMessageSource applicationMessages = new StaticMessageSource();
        applicationMessages.addMessage(
                "business.rule",
                Locale.ENGLISH,
                "Business rule failed for {0}"
        );

        CommonMessageResolver resolver = new CommonMessageResolver(applicationMessages);
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler(resolver))
                .setValidator(validator)
                .build();
    }

    @Test
    void handlesBusinessExceptionWithApplicationMessage() throws Exception {
        mockMvc.perform(get("/business").param("id", "product-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BUSINESS_RULE"))
                .andExpect(jsonPath("$.message").value("Business rule failed for product-1"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.path").value("/business"))
                .andExpect(jsonPath("$.fieldErrors").isEmpty());
    }

    @Test
    void handlesNotFoundExceptionInVietnamese() throws Exception {
        mockMvc.perform(get("/not-found").locale(Locale.forLanguageTag("vi-VN")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Không tìm thấy tài nguyên được yêu cầu"));
    }

    @Test
    void handlesRequestBodyValidationWithoutRejectedValue() throws Exception {
        mockMvc.perform(post("/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("name"))
                .andExpect(jsonPath("$.fieldErrors[0].message").value("Name is required"))
                .andExpect(content().string(not(containsString("rejectedValue"))));
    }

    @Test
    void handlesMethodParameterValidation() throws Exception {
        mockMvc.perform(get("/method-validation").param("quantity", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("quantity"));
    }

    @Test
    void handlesMalformedJsonAsBadRequest() throws Exception {
        mockMvc.perform(post("/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void hidesUnexpectedExceptionDetails() throws Exception {
        mockMvc.perform(get("/unexpected"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred"))
                .andExpect(content().string(not(containsString("secret-database-password"))))
                .andExpect(content().string(not(containsString("IllegalStateException"))));
    }

    @RestController
    static class TestController {

        @GetMapping("/business")
        void business(@RequestParam String id) {
            throw new BusinessException(new BusinessRuleError(), id);
        }

        @GetMapping("/not-found")
        void notFound() {
            throw new NotFoundException();
        }

        @PostMapping("/validate")
        void validate(@Valid @RequestBody CreateRequest request) {
        }

        @GetMapping("/method-validation")
        void validateMethodParameter(
                @RequestParam @Min(value = 1, message = "Quantity must be at least 1") int quantity
        ) {
        }

        @GetMapping("/unexpected")
        void unexpected() {
            throw new IllegalStateException("secret-database-password");
        }
    }

    record CreateRequest(
            @NotBlank(message = "Name is required") String name
    ) {
    }

    private record BusinessRuleError() implements ErrorCode {

        @Override
        public String code() {
            return "BUSINESS_RULE";
        }

        @Override
        public HttpStatus httpStatus() {
            return HttpStatus.BAD_REQUEST;
        }

        @Override
        public String messageKey() {
            return "business.rule";
        }

        @Override
        public String defaultMessage() {
            return "Business rule failed for {0}";
        }
    }
}
