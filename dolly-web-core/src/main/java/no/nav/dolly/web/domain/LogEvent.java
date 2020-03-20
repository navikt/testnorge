package no.nav.dolly.web.domain;

import lombok.Value;

import no.nav.dolly.web.provider.web.dto.LogEventDTO;

@Value
public class LogEvent {
    private final Level level;
    private final String event;
    private final String message;
    private final LogMetadata metadata;

    public LogEvent(LogEventDTO dto, String userAgent){
        metadata = new LogMetadata(userAgent);
        level = dto.getLevel();
        event = dto.getEvent();
        message = dto.getMessage();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder().append("event:").append(event).append(";");
        if(message != null) {
            builder.append("message:").append(message).append(";");
        }
        return builder.append(metadata.toString()).toString();
    }
}