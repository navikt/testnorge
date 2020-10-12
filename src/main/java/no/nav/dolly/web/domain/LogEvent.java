package no.nav.dolly.web.domain;

import lombok.Value;

import java.util.HashMap;
import java.util.Map;

import no.nav.dolly.web.provider.web.dto.LogEventDTO;
import no.nav.registre.testnorge.libs.dto.tilbakemeldingapi.v1.TilbakemeldingDTO;

@Value
public class LogEvent {
    private Level level;
    private String event;
    private String message;
    private LogMetadata metadata;
    private String uuid;
    private Rating rating;

    public LogEvent(LogEventDTO dto, String userAgent, String host) {
        metadata = new LogMetadata(userAgent, host);
        level = dto.getLevel();
        event = dto.getEvent();
        message = dto.getMessage();
        uuid = dto.getUuid();
        rating = dto.getRating();
    }

    public Map<String, String> toPropertyMap() {
        Map<String, String> properties = new HashMap<>();
        properties.put("log_event", event);
        properties.put("log_uuid", uuid);
        properties.put("log_host", metadata.getHost());
        properties.put("browser", metadata.getNameBrowser());
        properties.put("user-agent", metadata.getUserAgent());
        if (rating != null) {
            properties.put("rating", rating.name());
        }
        return properties;
    }

    public TilbakemeldingDTO toTilbakemeldingDTO() {
        return TilbakemeldingDTO
                .builder()
                .message(message)
                .title(event)
                .rating(toTilbakemeldingRating(rating))
                .build();
    }

    private no.nav.registre.testnorge.libs.dto.tilbakemeldingapi.v1.Rating toTilbakemeldingRating(Rating rating) {
        if (rating == null) {
            return null;
        }

        switch (rating) {
            case NEGATIVE:
                return no.nav.registre.testnorge.libs.dto.tilbakemeldingapi.v1.Rating.NEGATIVE;
            case POSITIVE:
                return no.nav.registre.testnorge.libs.dto.tilbakemeldingapi.v1.Rating.POSITIVE;
            default:
                return null;
        }
    }

}