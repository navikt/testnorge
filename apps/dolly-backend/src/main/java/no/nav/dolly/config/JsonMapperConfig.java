package no.nav.dolly.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

import java.io.IOException;
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
    @Primary
    public JsonMapper jsonMapper() {
        return JsonMapper.builder()
                .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(JsonInclude.Include.NON_NULL))
                .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
                .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
                .addModule(dollyDateTimeModule())
                .build();
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
        return node.isString() ? node.stringValue() : node.toString();
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

    // Følgende er lagt til for å støtte OpenSearch som pt benytter Jackson 2

    @Bean
    public ObjectMapper objectMapper() {
        var simpleModule = new com.fasterxml.jackson.databind.module.SimpleModule()
                .addDeserializer(LocalDateTime.class, new DollyLocalDateTimeDeserializer2())
                .addSerializer(LocalDateTime.class, new com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer(DateTimeFormatter.ISO_DATE_TIME))
                .addDeserializer(LocalDate.class, new DollyLocalDateDeserializer2())
                .addSerializer(LocalDate.class, new com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer(DateTimeFormatter.ISO_DATE))
                .addDeserializer(YearMonth.class, new DollyYearMonthDeserializer2())
                .addSerializer(YearMonth.class, new com.fasterxml.jackson.datatype.jsr310.ser.YearMonthSerializer(DateTimeFormatter.ofPattern(YEAR_MONTH)))
                .addDeserializer(ZonedDateTime.class, new DollyZonedDateTimeDeserializer2())
                .addSerializer(ZonedDateTime.class, new com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer(DateTimeFormatter.ISO_DATE_TIME))
                .addDeserializer(Instant.class, new DollyInstantDeserializer2());

        return com.fasterxml.jackson.databind.json.JsonMapper.builder()
                .configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(com.fasterxml.jackson.databind.DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
                .configure(com.fasterxml.jackson.databind.DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
                .enable(com.fasterxml.jackson.databind.MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .disable(com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .build()
                .registerModule(new JavaTimeModule())
                .registerModule(simpleModule);
    }

    private static class DollyYearMonthDeserializer2 extends JsonDeserializer<YearMonth> {

        @Override
        public YearMonth deserialize(com.fasterxml.jackson.core.JsonParser jsonParser, com.fasterxml.jackson.databind.DeserializationContext deserializationContext) throws IOException {
            com.fasterxml.jackson.databind.JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            if (isBlank(node.asText())) {
                return null;
            }
            return YearMonth.parse(node.asText(), DateTimeFormatter.ofPattern(YEAR_MONTH));
        }
    }

    private static class DollyZonedDateTimeDeserializer2 extends JsonDeserializer<ZonedDateTime> {

        @Override
        public ZonedDateTime deserialize(com.fasterxml.jackson.core.JsonParser jsonParser, com.fasterxml.jackson.databind.DeserializationContext deserializationContext) throws IOException {
            com.fasterxml.jackson.databind.JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            if (isBlank(node.asText())) {
                return null;
            }
            return ZonedDateTime.parse(node.asText(), DateTimeFormatter.ISO_DATE_TIME);
        }
    }

    private static class DollyLocalDateDeserializer2 extends JsonDeserializer<LocalDate> {

        @Override
        public LocalDate deserialize(com.fasterxml.jackson.core.JsonParser jsonParser, com.fasterxml.jackson.databind.DeserializationContext deserializationContext) throws IOException {
            com.fasterxml.jackson.databind.JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            if (isBlank(node.asText())) {
                return null;
            }
            var dateTime = node.asText().length() > 10 ? node.asText().substring(0, 10) : node.asText();
            return LocalDate.parse(dateTime);
        }
    }

    private static class DollyLocalDateTimeDeserializer2 extends JsonDeserializer<LocalDateTime> {

        @Override
        public LocalDateTime deserialize(com.fasterxml.jackson.core.JsonParser jsonParser, com.fasterxml.jackson.databind.DeserializationContext deserializationContext) throws IOException {
            com.fasterxml.jackson.databind.JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            if (isBlank(node.asText())) {
                return null;
            }
            var dateTime = node.asText().length() > 19 ? node.asText().substring(0, 19) : node.asText();
            return dateTime.length() > 10 ? LocalDateTime.parse(dateTime) : LocalDate.parse(dateTime).atStartOfDay();
        }
    }

    private static class DollyInstantDeserializer2 extends JsonDeserializer<Instant> {

        @Override
        public Instant deserialize(com.fasterxml.jackson.core.JsonParser jsonParser, com.fasterxml.jackson.databind.DeserializationContext deserializationContext) throws IOException {
            com.fasterxml.jackson.databind.JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            if (isBlank(node.asText())) {
                return null;
            }
            var timestamp = node.asText();
            return Instant.parse(timestamp.contains("Z") ? timestamp : timestamp + "Z");
        }
    }
}
