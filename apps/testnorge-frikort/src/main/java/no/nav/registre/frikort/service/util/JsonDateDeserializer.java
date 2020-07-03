package no.nav.registre.frikort.service.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class JsonDateDeserializer extends JsonDeserializer<LocalDateTime> {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH.mm.ss,SSSSSS");

    @Override public LocalDateTime deserialize(
            JsonParser jp,
            DeserializationContext ctxt
    ) throws IOException {
        return LocalDateTime.parse(jp.getValueAsString(), formatter);
    }
}