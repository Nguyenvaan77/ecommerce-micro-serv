package com.ecommerce.common.core.autoconfigure;

import com.ecommerce.common.core.i18n.CommonMessageResolver;
import com.ecommerce.common.core.web.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = CommonCoreAutoConfigurationImportTest.TestApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
class CommonCoreAutoConfigurationImportTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void discoversAutoConfigurationFromImportsMetadata() {
        assertThat(applicationContext.getBeansOfType(CommonMessageResolver.class)).hasSize(1);
        assertThat(applicationContext.getBeansOfType(GlobalExceptionHandler.class)).hasSize(1);
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    static class TestApplication {
    }
}
