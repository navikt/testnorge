package no.nav.testnav.libs.dto.pdlforvalter.v1.deserializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class StringOrListDeserializerTest {

    private ObjectMapper objectMapper;

    private static class TestWrapper {
        @JsonDeserialize(using = StringOrListDeserializer.class)
        public List<String> values;
    }

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldDeserializeSingleStringToList() throws JsonProcessingException {
        String json = "{\"values\": \"singleValue\"}";

        TestWrapper result = objectMapper.readValue(json, TestWrapper.class);

        assertThat(result.values).containsExactly("singleValue");
    }

    @Test
    void shouldDeserializeArrayOfStrings() throws JsonProcessingException {
        String json = "{\"values\": [\"first\", \"second\", \"third\"]}";

        TestWrapper result = objectMapper.readValue(json, TestWrapper.class);

        assertThat(result.values).containsExactly("first", "second", "third");
    }

    @Test
    void shouldDeserializeEmptyArrayToEmptyList() throws JsonProcessingException {
        String json = "{\"values\": []}";

        TestWrapper result = objectMapper.readValue(json, TestWrapper.class);

        assertThat(result.values).isEmpty();
    }

    @Test
    void shouldDeserializeNullToNull() throws JsonProcessingException {
        String json = "{\"values\": null}";

        TestWrapper result = objectMapper.readValue(json, TestWrapper.class);

        assertThat(result.values).isNull();
    }

    @Test
    void shouldDeserializeEmptyStringToEmptyList() throws JsonProcessingException {
        String json = "{\"values\": \"\"}";

        TestWrapper result = objectMapper.readValue(json, TestWrapper.class);

        assertThat(result.values).isEmpty();
    }

    @Test
    void shouldDeserializeSingleItemArray() throws JsonProcessingException {
        String json = "{\"values\": [\"onlyOne\"]}";

        TestWrapper result = objectMapper.readValue(json, TestWrapper.class);

        assertThat(result.values).containsExactly("onlyOne");
    }
}
