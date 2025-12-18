package no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.enums;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class JsonDateSerializer extends ValueSerializer<LocalDateTime> {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public void serialize(LocalDateTime date, JsonGenerator generator, SerializationContext context) {
        final var dateString = date.format(this.formatter);
        generator.writeString(dateString);
    }
}
