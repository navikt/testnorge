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
    private String uuid;

    @JsonProperty(required = true)
    private String event;

    @JsonProperty(required = true)
    private Level level;

    @JsonProperty
    private String message;

    @JsonProperty
    private Rating rating;

    @JsonProperty
    private Boolean isAnonym;
}