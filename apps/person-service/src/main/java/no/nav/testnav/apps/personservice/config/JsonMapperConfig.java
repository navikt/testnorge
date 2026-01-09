package no.nav.testnav.apps.personservice.config;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.module.SimpleModule;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Configuration
public class JsonMapperConfig {

    @Bean
    public JsonMapperBuilderCustomizer jsonMapperBuilderCustomizer(SimpleModule personServiceDateTimeModule) {
        return builder -> builder
                .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .addModule(personServiceDateTimeModule);
    }

    @Bean
    public SimpleModule personServiceDateTimeModule() {
        return new SimpleModule("PersonServiceDateTimeModule")
                .addDeserializer(LocalDate.class, new FlexibleLocalDateDeserializer())
                .addDeserializer(LocalDateTime.class, new FlexibleLocalDateTimeDeserializer())
                .addDeserializer(Instant.class, new FlexibleInstantDeserializer());
    }

    private static class FlexibleLocalDateDeserializer extends ValueDeserializer<LocalDate> {
        @Override
        public LocalDate deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
            JsonNode node = jsonParser.readValueAsTree();
            if (isBlank(node.asText())) {
                return null;
            }
            var dateTime = node.asText().length() > 10 ? node.asText().substring(0, 10) : node.asText();
            return LocalDate.parse(dateTime);
        }
    }

    private static class FlexibleLocalDateTimeDeserializer extends ValueDeserializer<LocalDateTime> {
        @Override
        public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
            JsonNode node = jsonParser.readValueAsTree();
            if (isBlank(node.asText())) {
                return null;
            }
            var dateTime = node.asText().length() > 19 ? node.asText().substring(0, 19) : node.asText();
            return dateTime.length() > 10 ? LocalDateTime.parse(dateTime) : LocalDate.parse(dateTime).atStartOfDay();
        }
    }

    private static class FlexibleInstantDeserializer extends ValueDeserializer<Instant> {
        @Override
        public Instant deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
            JsonNode node = jsonParser.readValueAsTree();
            if (isBlank(node.asText())) {
                return null;
            }
            var timestamp = node.asText();
            return Instant.parse(timestamp.contains("Z") ? timestamp : timestamp + "Z");
        }
    }
}

