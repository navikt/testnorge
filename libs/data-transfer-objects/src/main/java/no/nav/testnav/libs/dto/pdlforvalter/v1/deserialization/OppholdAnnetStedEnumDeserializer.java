package no.nav.testnav.libs.dto.pdlforvalter.v1.deserialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.TextNode;
import no.nav.testnav.libs.dto.pdlforvalter.v1.AdresseDTO.OppholdAnnetSted;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;


public class OppholdAnnetStedEnumDeserializer extends StdDeserializer<OppholdAnnetSted> {

    public OppholdAnnetStedEnumDeserializer() {
        super(OppholdAnnetSted.class);
    }

    @Override
    public OppholdAnnetSted deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {

        var node = (TextNode) jsonParser.getCodec().readTree(jsonParser);

        var words = StringUtils.splitByCharacterTypeCamelCase(node.asText());
        var name = String.join("_", words).toUpperCase();

        return OppholdAnnetSted.valueOf(name);
    }
}