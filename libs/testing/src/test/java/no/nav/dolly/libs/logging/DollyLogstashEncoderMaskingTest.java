package no.nav.dolly.libs.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DollyLogstashEncoderMaskingTest {

    private static final String REAL_IDENT = "01018012345";
    private static final String REAL_IDENT_MASKED = "010180xxxxx";
    private static final String OTHER_REAL_IDENT = "02028054321";
    private static final String OTHER_REAL_IDENT_MASKED = "020280xxxxx";
    private static final String SYNTHETIC_IDENT = "01418012345";
    private static final String SYNTHETIC_IDENT_MARKED = "01418012345x";
    private static final String TOKEN = "eyJhbGciOi.payload-part_1.signature";

    @Test
    void shouldMaskRealIdentInMessage() {
        var json = encode(event("Hentet person " + REAL_IDENT, Map.of(), null), new DollyLogstashEncoder());

        assertThat(json.get("message").asString())
                .isEqualTo("Hentet person " + REAL_IDENT_MASKED)
                .doesNotContain(REAL_IDENT);
    }

    @Test
    void shouldMarkSyntheticIdentInMessage() {
        var json = encode(event("Hentet person " + SYNTHETIC_IDENT, Map.of(), null), new DollyLogstashEncoder());

        assertThat(json.get("message").asString()).isEqualTo("Hentet person " + SYNTHETIC_IDENT_MARKED);
    }

    @Test
    void shouldNotMaskNumbersWithOtherLengthThanEleven() {
        var json = encode(event("Ordre 1234567890 og 123456789012", Map.of(), null), new DollyLogstashEncoder());

        assertThat(json.get("message").asString()).isEqualTo("Ordre 1234567890 og 123456789012");
    }

    @Test
    void shouldMaskBearerTokenInMessage() {
        var json = encode(event("Authorization: Bearer " + TOKEN, Map.of(), null), new DollyLogstashEncoder());

        assertThat(json.get("message").asString())
                .isEqualTo("Authorization: Bearer token")
                .doesNotContain(TOKEN);
    }

    @Test
    void shouldMaskIdentsAndTokensInContext() {
        var context = Map.of(
                "URI", "/api/v1/personer/" + REAL_IDENT,
                "Query-Params", "{ident=[" + SYNTHETIC_IDENT + "]}",
                "Body", "{\"token\":\"Bearer " + TOKEN + "\"}");

        var json = encode(event("melding", context, null), new DollyLogstashEncoder());

        var loggedContext = json.get("context");
        assertThat(loggedContext.get("URI").asString()).isEqualTo("/api/v1/personer/" + REAL_IDENT_MASKED);
        assertThat(loggedContext.get("Query-Params").asString()).isEqualTo("{ident=[" + SYNTHETIC_IDENT_MARKED + "]}");
        assertThat(loggedContext.get("Body").asString()).isEqualTo("{\"token\":\"Bearer token\"}");
        assertThat(loggedContext.toString()).doesNotContain(REAL_IDENT, TOKEN);
    }

    @Test
    void shouldMaskIdentsAndTokensInStackTrace() {
        var cause = new IllegalArgumentException("Ugyldig ident " + OTHER_REAL_IDENT);
        var exception = new IllegalStateException("Feil for " + REAL_IDENT + " med Bearer " + TOKEN, cause);
        exception.addSuppressed(new IllegalStateException("Undertrykt " + REAL_IDENT));

        var json = encode(event("Kall feilet", Map.of(), exception), new DollyLogstashEncoder());

        var stackTrace = json.get("stack_trace").asString();
        assertThat(stackTrace)
                .contains("Feil for " + REAL_IDENT_MASKED + " med Bearer token")
                .contains("Ugyldig ident " + OTHER_REAL_IDENT_MASKED)
                .contains("Undertrykt " + REAL_IDENT_MASKED)
                .doesNotContain(REAL_IDENT, OTHER_REAL_IDENT, TOKEN);
    }

    @Test
    void shouldMaskStackTraceBeforeTruncating() {
        var exception = new IllegalStateException(REAL_IDENT);
        var prefixLength = (IllegalStateException.class.getName() + ": ").length();
        var encoder = new DollyLogstashEncoder();
        encoder.setMaxStackTraceLength(prefixLength + 7);

        var json = encode(event("Kall feilet", Map.of(), exception), encoder);

        assertThat(json.get("stack_trace").asString())
                .contains(REAL_IDENT.substring(0, 6) + "x")
                .doesNotContain(REAL_IDENT.substring(0, 7));
    }

    @Test
    void shouldWriteValidJsonWhenMessageIsNull() {
        var json = encode(event(null, Map.of(), null), new DollyLogstashEncoder());

        assertThat(json.get("message").isNull()).isTrue();
        assertThat(json.get("level").asString()).isEqualTo("INFO");
    }

    private static LoggingEvent event(String message, Map<String, String> context, Throwable throwable) {
        var event = new LoggingEvent();
        event.setLoggerContext(new LoggerContext());
        event.setLoggerName("test");
        event.setLevel(Level.INFO);
        event.setThreadName("main");
        event.setMessage(message);
        event.setMDCPropertyMap(context);
        event.setTimeStamp(0L);
        if (throwable != null) {
            event.setThrowableProxy(new ThrowableProxy(throwable));
        }
        return event;
    }

    private static JsonNode encode(LoggingEvent event, DollyLogstashEncoder encoder) {
        var output = new String(encoder.encode(event), StandardCharsets.UTF_8);
        assertThat(output).isNotBlank();
        return JsonMapper.builder().build().readTree(output);
    }
}
