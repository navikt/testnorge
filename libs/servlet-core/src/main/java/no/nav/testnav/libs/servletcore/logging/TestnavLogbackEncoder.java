package no.nav.testnav.libs.servletcore.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import com.fasterxml.jackson.core.JsonFactory;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.encoder.LogstashEncoder;

import java.io.ByteArrayOutputStream;
import java.util.regex.Pattern;

@Slf4j
public class TestnavLogbackEncoder extends LogstashEncoder {

    // matches exactly 11 digits (\\d{11}) that are not immediately preceded ((?<!\\d)) or followed ((?!\\d)) by another digit.
    private final Pattern pattern = Pattern.compile("(?<!\\d)\\d{11}(?!\\d)");

    @SneakyThrows
    @Override
    public byte[] encode(ILoggingEvent event) {
        var outputStream = new ByteArrayOutputStream();

        var generator = new JsonFactory().createGenerator(outputStream);

        generator.writeStartObject();

        generator.writeStringField("@timestamp", new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSSXXX")
                .format(new java.util.Date(event.getTimeStamp())));
        generator.writeStringField("message", formatMessage(event.getFormattedMessage()));
        generator.writeStringField("logger_name", event.getLoggerName());
        generator.writeStringField("thread_name", event.getThreadName());
        generator.writeStringField("level", event.getLevel().toString());

        if (event.getThrowableProxy() != null) {
            generator.writeStringField("stack_trace", ThrowableProxyUtil.asString(event.getThrowableProxy()));
        }

        generator.writeEndObject();

        generator.flush();
        outputStream.write('\n');

        return outputStream.toByteArray();
    }

    private String formatMessage(String message) {
        var matcher = pattern.matcher(message);

        var result = new StringBuilder();

        while (matcher.find()) {
            var match = matcher.group();
            if (match.charAt(2) == '0' || match.charAt(2) == '1') {
                var replacement = match.substring(0, 6) + "xxxxx";
                matcher.appendReplacement(result, replacement);
            }
        }
        matcher.appendTail(result);

        return result.toString();
    }
}