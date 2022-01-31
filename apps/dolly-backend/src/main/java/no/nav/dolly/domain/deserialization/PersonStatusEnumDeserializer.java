package no.nav.dolly.domain.deserialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.TextNode;
import no.nav.dolly.domain.PdlPerson.FolkeregisterPersonstatus.Personstatus;

import java.io.IOException;


public class PersonStatusEnumDeserializer extends StdDeserializer<Personstatus> {

    public PersonStatusEnumDeserializer() {
        super(Personstatus.class);
    }

    @Override
    public Personstatus deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {

        var node = (TextNode) jsonParser.getCodec().readTree(jsonParser);

        return Personstatus.getEnum(node.asText());
    }
}