package com.ecommerce.common.core.i18n;

import com.ecommerce.common.core.error.ErrorCode;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Objects;

public final class CommonMessageResolver {

    private final MessageSource applicationMessageSource;
    private final MessageSource commonMessageSource;

    public CommonMessageResolver(MessageSource applicationMessageSource) {
        this.applicationMessageSource =
                Objects.requireNonNull(applicationMessageSource, "applicationMessageSource must not be null");

        ResourceBundleMessageSource bundledMessages = new ResourceBundleMessageSource();
        bundledMessages.setBasename("common-messages");
        bundledMessages.setDefaultEncoding("UTF-8");
        bundledMessages.setFallbackToSystemLocale(false);
        this.commonMessageSource = bundledMessages;
    }

    public String resolve(ErrorCode errorCode, Locale locale, Object... messageArgs) {
        Objects.requireNonNull(errorCode, "errorCode must not be null");
        Locale targetLocale = locale == null ? Locale.ENGLISH : locale;
        Object[] args = messageArgs == null ? new Object[0] : messageArgs;

        String applicationMessage =
                applicationMessageSource.getMessage(errorCode.messageKey(), args, null, targetLocale);
        if (applicationMessage != null) {
            return applicationMessage;
        }

        String commonMessage = commonMessageSource.getMessage(errorCode.messageKey(), args, null, targetLocale);
        if (commonMessage != null) {
            return commonMessage;
        }

        return new MessageFormat(errorCode.defaultMessage(), targetLocale).format(args);
    }
}
