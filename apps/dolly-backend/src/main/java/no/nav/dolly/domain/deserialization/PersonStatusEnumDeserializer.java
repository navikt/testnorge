package no.nav.dolly.domain.deserialization;

import no.nav.dolly.domain.PdlPerson.FolkeregisterPersonstatus.Personstatus;
import org.apache.commons.lang3.StringUtils;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;

public class PersonStatusEnumDeserializer extends ValueDeserializer<Personstatus> {

    @Override
    public Personstatus deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
        JsonNode node = jsonParser.readValueAsTree();
        String text = getNodeText(node);
        if (StringUtils.isBlank(text)) {
            return null;
        }
        var words = StringUtils.splitByCharacterTypeCamelCase(text);
        var name = String.join("_", words).toUpperCase();

        return Personstatus.valueOf(name);
    }

    private static String getNodeText(JsonNode node) {
        if (node == null || node.isMissingNode() || node.isNull()) {
            return null;
        }
        return node.isString() ? node.asString() : node.toString();
    }
}