package no.nav.testnav.libs.reactivecore.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;
import com.fasterxml.jackson.core.JsonGenerator;
import net.logstash.logback.composite.JsonProvider;
import net.logstash.logback.composite.loggingevent.MessageJsonProvider;
import net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestnavLogbackEncoder extends LoggingEventCompositeJsonEncoder {
    private final Pattern pattern = Pattern.compile("(?<!\\d)\\d{11}(?!\\d)");

    public TestnavLogbackEncoder() {
        JsonProvider<ILoggingEvent> provider = new LoggingMessageJsonProvider();
        provider.setContext(getContext());
        provider.start();
        getProviders().addProvider(provider);
    }

    private class LoggingMessageJsonProvider extends MessageJsonProvider {
        @Override
        public void writeTo(JsonGenerator generator, ILoggingEvent event) throws IOException {
            String originalMessage = event.getFormattedMessage();
            Matcher matcher = pattern.matcher(originalMessage);

            if (matcher.find()) {
                StringBuilder result = new StringBuilder();
                matcher.reset();

                while (matcher.find()) {
                    String match = matcher.group();
                    if (match.charAt(2) == '0' || match.charAt(2) == '1') {
                        String replacement = match.substring(0, 6) + "xxxxx";
                        matcher.appendReplacement(result, replacement);
                    }
                }
                matcher.appendTail(result);

                LoggingEvent modifiedEvent = new LoggingEvent();
                modifiedEvent.setLoggerName(event.getLoggerName());
                modifiedEvent.setLoggerContextRemoteView(event.getLoggerContextVO());
                modifiedEvent.setThreadName(event.getThreadName());
                modifiedEvent.setLevel(event.getLevel());
                modifiedEvent.setMessage(result.toString());
                modifiedEvent.setArgumentArray(event.getArgumentArray());
                if (event.getThrowableProxy() != null) {
                    modifiedEvent.setThrowableProxy((ThrowableProxy) event.getThrowableProxy());
                }
                modifiedEvent.setTimeStamp(event.getTimeStamp());

                super.writeTo(generator, modifiedEvent);
            } else {
                super.writeTo(generator, event);
            }
        }
    }
}