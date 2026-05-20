package no.nav.testnav.libs.dto.pdlforvalter.v1;

import no.nav.testnav.libs.dto.pdlforvalter.v1.AdresseDTO.OppholdAnnetSted;
import org.apache.commons.lang3.StringUtils;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;

public class OppholdAnnetStedEnumDeserializer extends ValueDeserializer<OppholdAnnetSted> {

    @Override
    public OppholdAnnetSted deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {

        var node = (JsonNode) jsonParser.readValueAsTree();
        var text = node.stringValue(null);
        if (text == null) {
            text = node.asString();
        }

        var words = StringUtils.splitByCharacterTypeCamelCase(text);
        var name = String.join("_", words).toUpperCase().replaceAll("(_)+", "_");

        return OppholdAnnetSted.valueOf(name);
    }
}