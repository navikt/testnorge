package no.nav.testnav.libs.dto.pdlforvalter.v1;

import no.nav.testnav.libs.dto.pdlforvalter.v1.AdresseDTO.OppholdAnnetSted;
import org.apache.commons.lang3.StringUtils;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.deser.std.StdScalarDeserializer;

public class OppholdAnnetStedEnumDeserializer extends StdScalarDeserializer<OppholdAnnetSted> {

    public OppholdAnnetStedEnumDeserializer() {
        super(OppholdAnnetSted.class);
    }

    @Override
    public OppholdAnnetSted deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {

        JsonNode node = jsonParser.readValueAsTree();

        var words = StringUtils.splitByCharacterTypeCamelCase(node.asText());
        var name = String.join("_", words).toUpperCase().replaceAll("(_)+", "_");

        return OppholdAnnetSted.valueOf(name);
    }
}