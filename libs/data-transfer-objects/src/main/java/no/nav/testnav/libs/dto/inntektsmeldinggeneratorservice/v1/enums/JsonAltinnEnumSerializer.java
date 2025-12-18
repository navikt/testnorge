package no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.enums;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ser.std.StdScalarSerializer;

public class JsonAltinnEnumSerializer extends StdScalarSerializer<AltinnEnum> {

    public JsonAltinnEnumSerializer() {
        super(AltinnEnum.class);
    }

    public JsonAltinnEnumSerializer(Class<AltinnEnum> t) {
        super(t);
    }

    @Override
    public void serialize(
            AltinnEnum altinnEnum,
            JsonGenerator generator,
            SerializationContext context
    ) {
        generator.writeString(altinnEnum.getValue());
    }
}
