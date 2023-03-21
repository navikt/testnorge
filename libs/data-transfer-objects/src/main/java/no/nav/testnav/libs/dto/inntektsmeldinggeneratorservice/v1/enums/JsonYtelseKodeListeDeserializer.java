package no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.enums;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.Arrays;


public class JsonYtelseKodeListeDeserializer extends StdDeserializer<YtelseKoder> {
    protected JsonYtelseKodeListeDeserializer() {
        super(YtelseKoder.class);
    }

    @Override
    public YtelseKoder deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        String itemName = node.asText();

        return Arrays.stream(YtelseKoder.values())
                .filter(yrke -> yrke.name().equals(itemName))
                .findFirst()
                .orElseGet(() -> Arrays.stream(YtelseKoder.values())
                        .filter(yrke -> yrke.getValue().equals(itemName))
                        .findFirst()
                        .orElseThrow()
                );

    }
}
