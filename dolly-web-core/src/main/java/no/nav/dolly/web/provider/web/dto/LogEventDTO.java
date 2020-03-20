package no.nav.dolly.web.provider.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Value;

import no.nav.dolly.web.domain.Level;

@Value
@NoArgsConstructor(force = true)
public class LogEventDTO {

    @JsonProperty(required = true)
    private final String event;

    @JsonProperty(required = true)
    private final Level level;

    @JsonProperty
    private final String message;
}