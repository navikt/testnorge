package no.nav.dolly.web.provider.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Value;

import no.nav.dolly.web.domain.Level;
import no.nav.dolly.web.domain.Rating;

@Value
@NoArgsConstructor(force = true)
public class LogEventDTO {

    @JsonProperty(required = true)
    String uuid;

    @JsonProperty(required = true)
    String event;

    @JsonProperty(required = true)
    Level level;

    @JsonProperty
    String message;

    @JsonProperty
    Rating rating;

    @JsonProperty
    Boolean isAnonym;
}