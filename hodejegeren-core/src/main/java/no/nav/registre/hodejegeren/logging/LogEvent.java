package no.nav.registre.hodejegeren.logging;

import lombok.Value;

import java.util.HashMap;
import java.util.Map;

@Value
public class LogEvent {

    private Level level;
    private String message;
    private String keyword;

    public LogEvent(
            LogEventDTO dto
    ) {
        level = dto.getLevel();
        message = dto.getMessage();
        keyword = dto.getKeyword();
    }

    public Map<String, String> toPropertyMap() {
        Map<String, String> properties = new HashMap<>();
        properties.put("keyword", keyword);
        return properties;
    }
}