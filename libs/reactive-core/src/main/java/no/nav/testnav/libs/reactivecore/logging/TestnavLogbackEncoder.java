package no.nav.testnav.libs.reactivecore.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.composite.JsonProvider;
import net.logstash.logback.composite.loggingevent.MessageJsonProvider;
import net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder;

import java.io.IOException;
import java.util.regex.Pattern;

@Slf4j
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
            var originalMessage = event.getFormattedMessage();
            var matcher = pattern.matcher(originalMessage);

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

                generator.writeStartObject();
                generator.writeStringField("@timestamp", new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").format(new java.util.Date(event.getTimeStamp())));
                generator.writeStringField("message", result.toString());
                generator.writeStringField("logger_name", event.getLoggerName());
                generator.writeStringField("thread_name", event.getThreadName());
                generator.writeStringField("level", event.getLevel().toString());
                generator.writeNumberField("level_value", event.getLevel().toInt());
                if (event.getThrowableProxy() != null) {
                    generator.writeStringField("stack_trace", ThrowableProxyUtil.asString(event.getThrowableProxy()));
                }
                generator.writeEndObject();

            } else {
                super.writeTo(generator, event);
            }
        }
    }
}