package no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.enums;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

import java.time.LocalDateTime;

public class JsonDateDeserializer extends ValueDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonParser jp, DeserializationContext ctxt) {
        return LocalDateTime.parse(jp.getValueAsString());
    }
}