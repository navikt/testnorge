package no.nav.dolly.config;

import no.nav.testnav.libs.dto.jackson.v1.CaseInsensitiveEnumModule;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.module.SimpleModule;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Configuration
public class JsonMapperConfig {

    private static final String YEAR_MONTH = "yyyy-MM";

    @Bean
    public JsonMapperBuilderCustomizer jsonMapperBuilderCustomizer(CaseInsensitiveEnumModule caseInsensitiveEnumModule, SimpleModule dollyDateTimeModule) {
        return builder -> builder
                .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .addModule(caseInsensitiveEnumModule)
                .addModule(dollyDateTimeModule);
    }

    @Bean
    @ConditionalOnMissingBean
    public CaseInsensitiveEnumModule caseInsensitiveEnumModule() {
        return new CaseInsensitiveEnumModule();
    }

    @Bean
    public SimpleModule dollyDateTimeModule() {
        return new SimpleModule("dollyDateTimeModule")
                .addDeserializer(LocalDateTime.class, new DollyLocalDateTimeDeserializer())
                .addSerializer(LocalDateTime.class, new LocalDateTimeSerializer())
                .addDeserializer(LocalDate.class, new DollyLocalDateDeserializer())
                .addSerializer(LocalDate.class, new LocalDateSerializer())
                .addDeserializer(YearMonth.class, new DollyYearMonthDeserializer())
                .addSerializer(YearMonth.class, new YearMonthSerializer())
                .addDeserializer(ZonedDateTime.class, new DollyZonedDateTimeDeserializer())
                .addSerializer(ZonedDateTime.class, new ZonedDateTimeSerializer())
                .addDeserializer(Instant.class, new DollyInstantDeserializer());
    }

    private static String getNodeText(JsonNode node) {
        if (node == null || node.isMissingNode() || node.isNull()) {
            return null;
        }
        return node.isTextual() ? node.textValue() : node.toString();
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

    private static class YearMonthSerializer extends ValueSerializer<YearMonth> {
        @Override
        public void serialize(YearMonth value, JsonGenerator gen, SerializationContext serializers) {
            gen.writeString(value.format(DateTimeFormatter.ofPattern(YEAR_MONTH)));
        }
    }

    private static class ZonedDateTimeSerializer extends ValueSerializer<ZonedDateTime> {
        @Override
        public void serialize(ZonedDateTime value, JsonGenerator gen, SerializationContext serializers) {
            gen.writeString(value.format(DateTimeFormatter.ISO_DATE_TIME));
        }
    }

    private static class DollyYearMonthDeserializer extends ValueDeserializer<YearMonth> {
        @Override
        public YearMonth deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
            JsonNode node = jsonParser.readValueAsTree();
            String text = getNodeText(node);
            if (isBlank(text)) {
                return null;
            }
            return YearMonth.parse(text, DateTimeFormatter.ofPattern(YEAR_MONTH));
        }
    }

    private static class DollyZonedDateTimeDeserializer extends ValueDeserializer<ZonedDateTime> {
        @Override
        public ZonedDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
            JsonNode node = jsonParser.readValueAsTree();
            String text = getNodeText(node);
            if (isBlank(text)) {
                return null;
            }
            return ZonedDateTime.parse(text, DateTimeFormatter.ISO_DATE_TIME);
        }
    }

    private static class DollyLocalDateDeserializer extends ValueDeserializer<LocalDate> {
        @Override
        public LocalDate deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
            JsonNode node = jsonParser.readValueAsTree();
            String text = getNodeText(node);
            if (isBlank(text)) {
                return null;
            }
            var dateTime = text.length() > 10 ? text.substring(0, 10) : text;
            return LocalDate.parse(dateTime);
        }
    }

    private static class DollyLocalDateTimeDeserializer extends ValueDeserializer<LocalDateTime> {
        @Override
        public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
            JsonNode node = jsonParser.readValueAsTree();
            String text = getNodeText(node);
            if (isBlank(text)) {
                return null;
            }
            var dateTime = text.length() > 19 ? text.substring(0, 19) : text;
            return dateTime.length() > 10 ? LocalDateTime.parse(dateTime) : LocalDate.parse(dateTime).atStartOfDay();
        }
    }

    private static class DollyInstantDeserializer extends ValueDeserializer<Instant> {
        @Override
        public Instant deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
            JsonNode node = jsonParser.readValueAsTree();
            String text = getNodeText(node);
            if (isBlank(text)) {
                return null;
            }
            return Instant.parse(text.contains("Z") ? text : text + "Z");
        }
    }
}
