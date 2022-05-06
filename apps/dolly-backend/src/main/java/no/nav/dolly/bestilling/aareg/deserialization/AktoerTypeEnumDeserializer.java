package no.nav.dolly.bestilling.aareg.deserialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.TextNode;
import no.nav.dolly.bestilling.aareg.domain.Aktoer;
import no.nav.testnav.libs.dto.pdlforvalter.v1.AdresseDTO.OppholdAnnetSted;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public class AktoerTypeEnumDeserializer extends StdDeserializer<Aktoer> {

    public AktoerTypeEnumDeserializer() {
        super(OppholdAnnetSted.class);
    }

    @Override
    public Aktoer deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {

        var node = (TextNode) jsonParser.getCodec().readTree(jsonParser);

        var words = StringUtils.splitByCharacterTypeCamelCase(node.asText());
        var name = String.join("_", words).toUpperCase().replaceAll("(_)+", "_");

        return Aktoer.valueOf(name);
    }
}