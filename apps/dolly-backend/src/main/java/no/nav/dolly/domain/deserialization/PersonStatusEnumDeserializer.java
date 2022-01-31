package no.nav.dolly.domain.deserialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import no.nav.dolly.domain.PdlPerson;

import java.io.IOException;


public class PersonStatusEnumDeserializer extends StdDeserializer<PdlPerson.FolkeregisterPersonstatus.Personstatus> {

    public PersonStatusEnumDeserializer() {
        super(PdlPerson.FolkeregisterPersonstatus.Personstatus.class);
    }

    @Override
    public PdlPerson.FolkeregisterPersonstatus.Personstatus deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {

        var node = jsonParser.getCodec().readTree(jsonParser);

//        String unit = node.get("unit").asText();
//        double meters = node.get("meters").asDouble();
//
//        for (Distance distance : Distance.values()) {
//
//            if (distance.getUnit().equals(unit) && Double.compare(
//                    distance.getMeters(), meters) == 0) {
//                return distance;
//            }
//        }

        return null;
    }
}