package no.nav.testnav.libs.reactivecore.config;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.apache.commons.lang3.StringUtils.isBlank;

@AutoConfiguration
public class JacksonAutoConfiguration {

    private static final String YEAR_MONTH = "yyyy-MM";

    // Følgende er lagt til for å støtte OpenSearch som pt benytter Jackson 2

    @Bean
    @ConditionalOnMissingBean(com.fasterxml.jackson.databind.ObjectMapper.class)
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
