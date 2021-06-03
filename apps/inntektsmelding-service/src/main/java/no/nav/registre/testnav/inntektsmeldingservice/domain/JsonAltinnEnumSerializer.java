package no.nav.registre.testnav.inntektsmeldingservice.domain;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

import no.nav.registre.testnav.inntektsmeldingservice.consumer.dto.inntektsmeldinggenerator.v1.enums.AltinnEnum;


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
