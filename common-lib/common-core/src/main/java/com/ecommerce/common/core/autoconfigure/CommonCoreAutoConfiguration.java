package com.ecommerce.common.core.autoconfigure;

import com.ecommerce.common.core.i18n.CommonMessageResolver;
import com.ecommerce.common.core.web.GlobalExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class CommonCoreAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    CommonMessageResolver commonMessageResolver(MessageSource messageSource) {
        return new CommonMessageResolver(messageSource);
    }

    @Bean
    @ConditionalOnMissingBean
    GlobalExceptionHandler globalExceptionHandler(CommonMessageResolver messageResolver) {
        return new GlobalExceptionHandler(messageResolver);
    }
}
