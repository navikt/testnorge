package no.nav.dolly.domain.resultset.util;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class JsonZonedDateTimeDeserializer extends JsonDeserializer<ZonedDateTime> {

    @Override public ZonedDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {

        if (isBlank(jsonParser.getValueAsString())) {
            return null;
        }
        if (jsonParser.getValueAsString().length() > 19 || jsonParser.getValueAsString().contains("Z")) {
            return ZonedDateTime.parse(jsonParser.getValueAsString());
        } else {
            return LocalDateTime.parse(jsonParser.getValueAsString()).atZone(ZoneId.systemDefault());
        }
    }
}