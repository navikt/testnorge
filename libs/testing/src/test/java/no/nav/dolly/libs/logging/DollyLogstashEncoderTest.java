package no.nav.dolly.libs.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggingEvent;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DollyLogstashEncoderTest {

    @Test
    void shouldWriteMillisecondsAsFractionOfSecondInTimestamp() {

        var timestamp = Instant.parse("2026-10-27T13:02:03.456Z");
        var event = new LoggingEvent();
        event.setLoggerContext(new LoggerContext());
        event.setLoggerName("test");
        event.setLevel(Level.INFO);
        event.setThreadName("main");
        event.setMessage("melding");
        event.setMDCPropertyMap(Map.of());
        event.setTimeStamp(timestamp.toEpochMilli());

        var json = new String(new DollyLogstashEncoder().encode(event), StandardCharsets.UTF_8);
        var loggedTimestamp = JsonMapper.builder().build().readTree(json).get("@timestamp").asString();

        assertThat(loggedTimestamp).contains(":03.456000000");
        assertThat(OffsetDateTime.parse(loggedTimestamp).toInstant()).isEqualTo(timestamp);
    }
}
