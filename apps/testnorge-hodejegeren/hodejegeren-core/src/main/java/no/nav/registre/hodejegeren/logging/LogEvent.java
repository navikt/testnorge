package no.nav.registre.hodejegeren.logging;

import lombok.Value;

import java.util.HashMap;
import java.util.Map;

@Value
public class LogEvent {

    private Level level;
    private String message;
    private String key;

    public LogEvent(
            LogEventDTO dto
    ) {
        level = dto.getLevel();
        message = dto.getMessage();
        key = dto.getKey();
    }

    public Map<String, String> toPropertyMap() {
        Map<String, String> properties = new HashMap<>();
        properties.put("key", key);
        return properties;
    }
}