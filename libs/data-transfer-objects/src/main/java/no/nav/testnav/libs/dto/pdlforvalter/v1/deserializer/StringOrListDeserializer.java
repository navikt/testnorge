package no.nav.testnav.libs.dto.pdlforvalter.v1.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StringOrListDeserializer extends JsonDeserializer<List<String>> {

    @Override
    public List<String> deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonToken token = parser.currentToken();

        if (token == JsonToken.VALUE_STRING) {
            String value = parser.getValueAsString();
            if (value == null || value.isEmpty()) {
                return Collections.emptyList();
            }
            return Collections.singletonList(value);
        }

        if (token == JsonToken.START_ARRAY) {
            List<String> result = new ArrayList<>();
            while (parser.nextToken() != JsonToken.END_ARRAY) {
                result.add(parser.getValueAsString());
            }
            return result;
        }

        if (token == JsonToken.VALUE_NULL) {
            return Collections.emptyList();
        }

        return Collections.emptyList();
    }
}

