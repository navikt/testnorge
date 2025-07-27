package no.nav.dolly.libs.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.encoder.LogstashEncoder;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Configuration of encoder:
 * <ul>
 * <li>{@code maxStackTraceLength}: Default 1000, set to <1 to disable truncation of stack trace altogether.</li>
 * <li>{@code addCauses}: Default {@code true}. Adds the cause(s) to the stack trace (with stack traces for each cause) Truncated according to above, filtered according to below.</li>
 * <li>{@code stackTraceIncludePrefix}: Default {@code "nav.no"}. Only include stack trace elements that starts with the given prefix.</li>
 * <li>{@code addSuppressed}: Default {@code true}. Adds any suppressed exceptions to the stack trace, subject to same filtering and truncation as the regular stack trace.</li>
 * </ul>
 */
@Setter
@Slf4j
public class DollyLogstashEncoder extends LogstashEncoder {

    // Matches exactly 11 digits (\\d{11}) that are not immediately preceded ((?<!\\d)) or followed ((?!\\d)) by another digit.
    private static final Pattern IDENT = Pattern.compile("(?<!\\d)\\d{11}(?!\\d)");
    private static final Pattern BEARER = Pattern.compile("Bearer [a-zA-Z0-9\\-_.]+");

    private final DateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSSXXX");

    private int maxStackTraceLength = 1000;
    private boolean addCauses = true;
    private String stackTraceIncludePrefix = "no.nav";
    private boolean addSuppressed = true;

    private String customFields;
    private boolean includeContext = true;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] encode(ILoggingEvent event) {

        var o = new ByteArrayOutputStream();
        try (var g = new JsonFactory().createGenerator(o)) {
            g.setCodec(objectMapper);
            g.writeStartObject();

            // Custom fields for logging to Team Logs.
            if (StringUtils.hasText(customFields)) {
                var fields = objectMapper.readValue(customFields, new TypeReference<Map<String, Object>>() {
                });
                for (var entry : fields.entrySet()) {
                    g.writeObjectField(entry.getKey(), entry.getValue());
                }
            }
            if (includeContext && Optional.ofNullable(event.getMDCPropertyMap()).isPresent()) {
                g.writeObjectField("context", event.getMDCPropertyMap());
            }

            g.writeStringField("@timestamp", timestamp.format(new Date(event.getTimeStamp())));
            g.writeStringField("message", maskMessage(event.getFormattedMessage()));
            g.writeStringField("logger_name", event.getLoggerName());
            g.writeStringField("thread_name", event.getThreadName());
            g.writeStringField("level", event.getLevel().toString());
            g.writeStringField("stack_trace", getStackTrace(event));
            g.writeEndObject();
            g.flush();
            o.write('\n');
        } catch (Exception e) {
            // Last resort; we don't have a functioning logger framework.
            System.err.println("Failed to encode log event:");
            e.printStackTrace(System.err);
        }
        return o.toByteArray();

    }

    private String getStackTrace(ILoggingEvent event) {
        return Optional
                .ofNullable(event.getThrowableProxy())
                .map(ThrowableProxy.class::cast)
                .map(ThrowableProxy::getThrowable)
                .map(throwable -> {
                    var writer = new StringWriter();
                    appendException(throwable, writer);
                    appendStackTraceOf(throwable, writer);
                    appendCauseOf(throwable, writer);
                    appendSuppressedOf(throwable, writer);
                    return truncated(writer.toString());
                })
                .orElse(null);
    }

    private String truncated(String s) {
        return maxStackTraceLength > 0 ? StringUtils.truncate(s, maxStackTraceLength) : s;
    }

    private void appendException(Throwable throwable, StringWriter writer) {
        writer
                .append(throwable.getClass().getName())
                .append(": ")
                .append(throwable.getMessage())
                .append("\n");
    }

    private void appendStackTraceOf(Throwable throwable, StringWriter writer) {
        Optional
                .ofNullable(throwable.getStackTrace())
                .ifPresent(stackTraceElements -> Stream
                        .of(stackTraceElements)
                        .filter(this::shouldIncludeStackTraceElement)
                        .forEach(element -> writer
                                .append("\tat ")
                                .append(element.toString())
                                .append("\n")));
    }

    private boolean shouldIncludeStackTraceElement(StackTraceElement element) {
        return !StringUtils.hasText(stackTraceIncludePrefix) || element.toString().startsWith(stackTraceIncludePrefix);
    }

    private void appendSuppressedOf(Throwable throwable, StringWriter writer) {
        if (!addSuppressed) {
            return;
        }
        Optional
                .ofNullable(throwable.getSuppressed())
                .ifPresent(throwables -> Stream
                        .of(throwables)
                        .forEach(suppressed -> {
                            writer.append(suppressed.getMessage());
                            writer.append("\n");
                            appendStackTraceOf(suppressed, writer);
                        }));
    }

    private void appendCauseOf(Throwable throwable, StringWriter writer) {
        if (!addCauses) {
            return;
        }
        Optional
                .ofNullable(throwable.getCause())
                .ifPresent(cause -> {
                    writer
                            .append("caused by ")
                            .append(cause.getClass().getSimpleName())
                            .append(": ")
                            .append(cause.getMessage())
                            .append("\n");
                    appendStackTraceOf(cause, writer);
                    Optional
                            .ofNullable(cause.getCause())
                            .ifPresent(causeOfCause -> appendCauseOf(causeOfCause, writer));
                });
    }

    private static String maskMessage(String message) {
        if (message == null) {
            return null;
        }
        var masked = IDENT
                .matcher(message)
                .replaceAll(match ->
                        match.group().charAt(2) < '4' ?
                                match.group().substring(0, 6) + "xxxxx" :
                                match.group().substring(0, 11) + "x"
                );
        return BEARER
                .matcher(masked)
                .replaceAll("Bearer token");
    }

}
