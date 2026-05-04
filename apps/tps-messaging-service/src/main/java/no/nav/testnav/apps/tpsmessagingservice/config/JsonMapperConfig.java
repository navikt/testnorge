package no.nav.testnav.apps.tpsmessagingservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.cfg.EnumFeature;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Configuration
public class JsonMapperConfig {

    @Bean
    @Primary
    public JsonMapper jsonMapper() {

        return JsonMapper.builder()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
                .configure(EnumFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true)
                .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .addModule(new SimpleModule("dollyDateTimeModule")
                        .addDeserializer(LocalDateTime.class, new DollyLocalDateTimeDeserializer())
                        .addSerializer(LocalDateTime.class, new LocalDateTimeSerializer())
                        .addDeserializer(LocalDate.class, new DollyLocalDateDeserializer())
                        .addSerializer(LocalDate.class, new LocalDateSerializer())
                        .addDeserializer(ZonedDateTime.class, new DollyZonedDateTimeDeserializer())
                        .addSerializer(ZonedDateTime.class, new ZonedDateTimeSerializer()))
                .build();
    }

    private static class LocalDateSerializer extends ValueSerializer<LocalDate> {
        @Override
        public void serialize(LocalDate value, JsonGenerator gen, SerializationContext serializers) {
            gen.writeString(value.format(DateTimeFormatter.ISO_DATE));
        }
    }

    private static class LocalDateTimeSerializer extends ValueSerializer<LocalDateTime> {
        @Override
        public void serialize(LocalDateTime value, JsonGenerator gen, SerializationContext serializers) {
            gen.writeString(value.format(DateTimeFormatter.ISO_DATE_TIME));
        }
    }

    private static class ZonedDateTimeSerializer extends ValueSerializer<ZonedDateTime> {
        @Override
        public void serialize(ZonedDateTime value, JsonGenerator gen, SerializationContext serializers) {
            gen.writeString(value.format(DateTimeFormatter.ISO_DATE_TIME));
        }
    }

    private static class DollyZonedDateTimeDeserializer extends ValueDeserializer<ZonedDateTime> {
        @Override
        public ZonedDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
            JsonNode node = jsonParser.readValueAsTree();
            if (isBlank(node.asString())) {
                return null;
            }
            return ZonedDateTime.parse(node.asString(), DateTimeFormatter.ISO_DATE_TIME);
        }
    }

    private static class DollyLocalDateDeserializer extends ValueDeserializer<LocalDate> {
        @Override
        public LocalDate deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
            JsonNode node = jsonParser.readValueAsTree();
            if (isBlank(node.asString())) {
                return null;
            }
            String dateTime = node.asString().length() > 10 ? node.asString().substring(0, 10) : node.asString();
            return LocalDate.parse(dateTime);
        }
    }

    private static class DollyLocalDateTimeDeserializer extends ValueDeserializer<LocalDateTime> {
        @Override
        public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
            JsonNode node = jsonParser.readValueAsTree();
            if (isBlank(node.asString())) {
                return null;
            }
            String dateTime = node.asString().length() > 19 ? node.asString().substring(0, 19) : node.asString();
            return dateTime.length() > 10 ? LocalDateTime.parse(dateTime) : LocalDate.parse(dateTime).atStartOfDay();
        }
    }
}
