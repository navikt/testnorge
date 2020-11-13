package no.nav.registre.testnorge.identservice.testdata.servicerutiner.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Locale;

import static no.nav.registre.testnorge.identservice.testdata.servicerutiner.message.MessageConstants.UNKNOWN_MESSAGE_KEY;


@Component
public class MessageProvider {

    @Autowired
    private MessageSource messageSource;

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageProvider.class);

    public String get(String messageKey) {
        return get(messageKey, (Object) null);
    }

    public String get(String messageKey, Object... arguments) {
        Locale locale = LocaleContextHolder.getLocale();

        try {
            return messageSource.getMessage(messageKey, arguments, locale);
        } catch (NoSuchMessageException exception) {
            writeToLog(messageKey, locale);
            return messageKey + " - " + StringUtils.arrayToCommaDelimitedString(arguments);
        }
    }

    private void writeToLog(String messageKey, Locale locale) {
        try {
            String[] messageKeyAsArray = { messageKey };
            LOGGER.warn(messageSource.getMessage(UNKNOWN_MESSAGE_KEY, messageKeyAsArray, locale));
        } catch (NoSuchMessageException exception) {
            LOGGER.error("Unable find the standard message, '{}', in messages.properties", UNKNOWN_MESSAGE_KEY);
        }
    }

}
