package no.nav.dolly.web.domain;

import lombok.Value;
import no.nav.dolly.web.provider.web.dto.LogEventDTO;
import no.nav.registre.testnorge.libs.dto.tilbakemeldingapi.v1.TilbakemeldingDTO;

import java.util.HashMap;
import java.util.Map;

@Value
public class LogEvent {
    Level level;
    String event;
    String message;
    LogMetadata metadata;
    String uuid;
    Rating rating;
    Boolean isAnonym;

    public LogEvent(LogEventDTO dto, String userAgent, String host) {
        metadata = new LogMetadata(userAgent, host);
        level = dto.getLevel();
        event = dto.getEvent();
        message = dto.getMessage();
        uuid = dto.getUuid();
        rating = dto.getRating();
        isAnonym = dto.getIsAnonym();
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
                .isAnonym(isAnonym)
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