package no.nav.testnav.libs.reactivecore.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;
import com.fasterxml.jackson.core.JsonFactory;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.encoder.LogstashEncoder;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.util.regex.Pattern;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.springframework.util.StringUtils.truncate;

/**
 * Config:
 * <ul>
 * <li>{@code maxStackTraceLength}: Default 480, set to <1 to disable truncation of stack trace altogether.</li>
 * <li>{@code addCauses}: If enabled, adds the cause(s) to the stack trace (with stack traces for each cause) Truncated according to above, filtered according to below.</li>
 * <li>{@code stackTraceIncludePrefix}: If set, only include stack trace elements that starts with the given prefix, e.g. "no.nav.testnav".</li>
 * </ul>
 * Copy of {@code no.nav.testnav.libs.servletcore.logging.TestnavLogbackEncoder}.
 * @see StringUtils#truncate(CharSequence, int)
 */
@Slf4j
@SuppressWarnings("java:S110")
public class TestnavLogbackEncoder extends LogstashEncoder {

    // matches exactly 11 digits (\\d{11}) that are not immediately preceded ((?<!\\d)) or followed ((?!\\d)) by another digit.
    private final Pattern pattern = Pattern.compile("(?<!\\d)\\d{11}(?!\\d)");

    @Setter
    private int maxStackTraceLength = 480;

    @Setter
    private boolean addCauses = false;

    @Setter
    private String stackTraceIncludePrefix = null;

    @SneakyThrows
    @Override
    public byte[] encode(ILoggingEvent event) {

        var outputStream = new ByteArrayOutputStream();
        try (var generator = new JsonFactory().createGenerator(outputStream)) {

            generator.writeStartObject();
            generator.writeStringField("@timestamp", new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSSXXX")
                    .format(new java.util.Date(event.getTimeStamp())));
            generator.writeStringField("message", formatMessage(event.getFormattedMessage()));
            generator.writeStringField("logger_name", event.getLoggerName());
            generator.writeStringField("thread_name", event.getThreadName());
            generator.writeStringField("level", event.getLevel().toString());
            generator.writeStringField("stack_trace", getStackTrace(event));
            generator.writeEndObject();

            generator.flush();
            outputStream.write('\n');
        }

        return outputStream.toByteArray();
    }

    private String getStackTrace(ILoggingEvent event) {
        if (nonNull(event.getThrowableProxy())) {
            var exception = (ThrowableProxy) event.getThrowableProxy();
            if (nonNull(exception.getThrowable())) {
                var writer = new StringWriter();
                appendStackTraceElements(exception.getThrowable().getStackTrace(), writer);
                appendStackTraceCauses(exception, writer);
                return maxStackTraceLength > 0 ?
                        truncate(writer.toString(), maxStackTraceLength) :
                        writer.toString();
            }
        }
        return null;
    }

    private void appendStackTraceElements(StackTraceElement[] elements, StringWriter writer) {
        for (StackTraceElement element : elements) {
            if (isNull(stackTraceIncludePrefix) || stackTraceIncludePrefix.isEmpty() || element.toString().startsWith(stackTraceIncludePrefix)) {
                writer
                        .append("\tat ")
                        .append(element.toString())
                        .append("\n");
            }
        }
    }

    private void appendStackTraceCauses(ThrowableProxy exception, StringWriter writer) {
        if (addCauses) {
            var cause = exception;
            while (!isNull(cause.getCause())) {
                cause = (ThrowableProxy) cause.getCause();
                writer
                        .append("caused by ")
                        .append(cause.getClassName())
                        .append(": ")
                        .append(cause.getMessage())
                        .append("\n");
                if (!isNull(cause.getThrowable())) {
                    appendStackTraceElements(cause.getThrowable().getStackTrace(), writer);
                }
            }
        }
    }

    private String formatMessage(String message) {
        var matcher = pattern.matcher(message);

        if (!matcher.find()) {
            return message;
        }

        matcher.reset();
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