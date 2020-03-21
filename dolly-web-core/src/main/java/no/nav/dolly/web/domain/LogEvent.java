package no.nav.dolly.web.domain;

import lombok.Value;

import java.util.HashMap;
import java.util.Map;

import no.nav.dolly.web.provider.web.dto.LogEventDTO;

@Value
public class LogEvent {
    private Level level;
    private String event;
    private String message;
    private LogMetadata metadata;
    private String uuid;

    public LogEvent(LogEventDTO dto, String userAgent) {
        metadata = new LogMetadata(userAgent);
        level = dto.getLevel();
        event = dto.getEvent();
        message = dto.getMessage();
        uuid = dto.getUuid();
    }

    public Map<String, String> toPropertyMap() {
        Map<String, String> properties = new HashMap<>();
        properties.put("log_event", event);
        properties.put("log_uuid", uuid);
        properties.put("browser", metadata.getNameBrowser());
        properties.put("user-agent", metadata.getUserAgent());
        return properties;
    }
}