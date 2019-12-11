package no.nav.dolly.domain.resultset.util;

import static java.util.Objects.nonNull;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class JsonZonedDateTimeDeserializer extends JsonDeserializer<ZonedDateTime> {

    @Override public ZonedDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {

        return nonNull(jsonParser.getValueAsString()) ?  LocalDateTime.parse(jsonParser.getValueAsString()).atZone(ZoneId.systemDefault()) : null;
    }
}