package no.nav.registre.inntekt.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import no.nav.registre.inntekt.consumer.rs.altinninntekt.dto.enums.AltinnEnum;

import java.io.IOException;

public class JsonAltinnEnumSerializer extends StdSerializer<AltinnEnum> {

    public JsonAltinnEnumSerializer() {
        super(AltinnEnum.class);
    }

    public JsonAltinnEnumSerializer(Class t) {
        super(t);
    }

    @Override
    public void serialize(
            AltinnEnum altinnEnum,
            JsonGenerator generator,
            SerializerProvider provider
    ) throws IOException, JsonProcessingException {
        generator.writeString(altinnEnum.getValue());
    }
}
