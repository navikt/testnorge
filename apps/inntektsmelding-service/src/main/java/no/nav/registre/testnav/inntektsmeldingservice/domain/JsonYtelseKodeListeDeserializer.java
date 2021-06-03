package no.nav.registre.testnav.inntektsmeldingservice.domain;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.Arrays;

import no.nav.registre.testnav.inntektsmeldingservice.consumer.dto.inntektsmeldinggenerator.v1.enums.YtelseKodeListe;


public class JsonYtelseKodeListeDeserializer extends StdDeserializer<YtelseKodeListe> {
    protected JsonYtelseKodeListeDeserializer() {
        super(YtelseKodeListe.class);
    }

    @Override
    public YtelseKodeListe deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        String itemName = node.asText();

        return Arrays.stream(YtelseKodeListe.values())
                .filter(yrke -> yrke.name().equals(itemName))
                .findFirst()
                .orElseGet(() -> Arrays.stream(YtelseKodeListe.values())
                        .filter(yrke -> yrke.getValue().equals(itemName))
                        .findFirst()
                        .orElseThrow()
                );

    }
}
