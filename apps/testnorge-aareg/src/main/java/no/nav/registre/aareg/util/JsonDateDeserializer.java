package no.nav.registre.aareg.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
public class JsonDateDeserializer extends JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        log.info("Prøver å parse: {}", jp.getText());
        return LocalDateTime.parse(jp.getValueAsString());
    }
}