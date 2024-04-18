package no.nav.testnav.apps.oppsummeringsdokumentservice.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.YearMonthSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    public ObjectMapper objectMapper() {

        var simpleModule = new SimpleModule()
                .addDeserializer(LocalDateTime.class, new DollyLocalDateTimeDeserializer())
                .addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ISO_DATE_TIME))
                .addDeserializer(LocalDate.class, new DollyLocalDateDeserializer())
                .addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ISO_DATE))
                .addDeserializer(YearMonth.class, new DollyYearMonthDeserializer())
                .addSerializer(YearMonth.class, new YearMonthSerializer(DateTimeFormatter.ofPattern(YEAR_MONTH)))
                .addDeserializer(ZonedDateTime.class, new DollyZonedDateTimeDeserializer())
                .addSerializer(ZonedDateTime.class, new ZonedDateTimeSerializer(DateTimeFormatter.ISO_DATE_TIME))
                .addDeserializer(Instant.class, new DollyInstantDeserializer());
        return JsonMapper
                .builder()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
                .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .build()
                .findAndRegisterModules()
                .registerModule(simpleModule);

    }

    private static class DollyYearMonthDeserializer extends JsonDeserializer<YearMonth> {

        @Override
        public YearMonth deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            if (isBlank(node.asText())) {
                return null;
            }
            return YearMonth.parse(node.asText(), DateTimeFormatter.ofPattern(YEAR_MONTH));
        }
    }

    private static class DollyZonedDateTimeDeserializer extends JsonDeserializer<ZonedDateTime> {

        @Override
        public ZonedDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            if (isBlank(node.asText())) {
                return null;
            }
            return ZonedDateTime.parse(node.asText(), DateTimeFormatter.ISO_DATE_TIME);
        }
    }

    private static class DollyLocalDateDeserializer extends JsonDeserializer<LocalDate> {

        @Override
        public LocalDate deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            if (isBlank(node.asText())) {
                return null;
            }
            var dateTime = node.asText().length() > 10 ? node.asText().substring(0, 10) : node.asText();
            if (!dateTime.contains("-")) {
                dateTime = "%s-%s-%s".formatted(dateTime.substring(0, 4), dateTime.substring(4, 6), dateTime.substring(6, 8));
            }
            return LocalDate.parse(dateTime);
        }
    }

    private static class DollyLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

        @Override
        public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            if (isBlank(node.asText())) {
                return null;
            }
            var dateTime = node.asText().length() > 19 ? node.asText().substring(0, 19) : node.asText();
            return dateTime.length() > 10 ? LocalDateTime.parse(dateTime) : LocalDate.parse(dateTime).atStartOfDay();
        }
    }

    private static class DollyInstantDeserializer extends JsonDeserializer<Instant> {

        /**
         * Input format 20210831T101647.269Z or 2021-08-31T10:16:47.269Z
         *
         */
        @Override
        public Instant deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {

            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            if (isBlank(node.asText())) {
                return null;
            }
            var instant = node.asText();
            if (!instant.contains("-") && instant.length() > 15) {
                instant = "%s-%s-%s:%s:%s".formatted(instant.substring(0, 4), instant.substring(4, 6),
                        instant.substring(6, 11), instant.substring(11, 13), instant.substring(13));
            }
            return Instant.parse(instant);
        }
    }
}
