package com.ecommerce.common.core.autoconfigure;

import com.ecommerce.common.core.i18n.CommonMessageResolver;
import com.ecommerce.common.core.web.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.support.StaticMessageSource;

import static org.assertj.core.api.Assertions.assertThat;

class CommonCoreAutoConfigurationTest {

    private final WebApplicationContextRunner contextRunner =
            new WebApplicationContextRunner()
                    .withConfiguration(AutoConfigurations.of(CommonCoreAutoConfiguration.class));

    @Test
    void registersCommonCoreBeansForServletApplications() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(CommonMessageResolver.class);
            assertThat(context).hasSingleBean(GlobalExceptionHandler.class);
        });
    }

    @Test
    void backsOffWhenApplicationProvidesCustomBeans() {
        CommonMessageResolver customResolver =
                new CommonMessageResolver(new StaticMessageSource());
        GlobalExceptionHandler customHandler =
                new GlobalExceptionHandler(customResolver);

        contextRunner
                .withBean(CommonMessageResolver.class, () -> customResolver)
                .withBean(GlobalExceptionHandler.class, () -> customHandler)
                .run(context -> {
                    assertThat(context).getBean(CommonMessageResolver.class).isSameAs(customResolver);
                    assertThat(context).getBean(GlobalExceptionHandler.class).isSameAs(customHandler);
                });
    }
}
