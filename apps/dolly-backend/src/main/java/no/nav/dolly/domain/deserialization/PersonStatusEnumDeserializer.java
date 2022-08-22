package no.nav.dolly.domain.deserialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.TextNode;
import no.nav.dolly.domain.PdlPerson.FolkeregisterPersonstatus.Personstatus;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;


public class PersonStatusEnumDeserializer extends StdDeserializer<Personstatus> {

    public PersonStatusEnumDeserializer() {
        super(Personstatus.class);
    }

    @Override
    public Personstatus deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {

        var node = (TextNode) jsonParser.getCodec().readTree(jsonParser);

        var words = StringUtils.splitByCharacterTypeCamelCase(node.asText());
        var name = String.join("_", words).toUpperCase();

        return Personstatus.valueOf(name);
    }
}