package no.nav.registre.testnorge.libs.dto.inntektsmeldinggeneratorservice.v1.enums;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class JsonDateSerializer extends JsonSerializer<LocalDateTime> {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public void serialize(LocalDateTime date, JsonGenerator generator, SerializerProvider arg2) throws IOException {
        final var dateString = date.format(this.formatter);
        generator.writeString(dateString);
    }
}
