package no.nav.registre.inntekt.config;

import static org.apache.commons.lang3.StringUtils.isBlank;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class JsonMapperConfig {

    // Timestrings can be on the format "1970-01-01T12:00:00.000+01:00"
    // (I.e. "YYYY-MM-DDThh:mm:ss.sss+hh:mm")
    private static final int TIME_STRING_LENGTH_TO_SECOND = 19;
    private static final int TIME_STRING_LENGTH_TO_DATE = 10;

    @Bean
    public ObjectMapper objectMapper() {
        var objectMapper = new ObjectMapper();

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        var simpleModule = new SimpleModule();
        simpleModule.addDeserializer(LocalDateTime.class, new DollyLocalDateTimeDeserializer());
        simpleModule.addDeserializer(LocalDate.class, new DollyLocalDateDeserializer());
        simpleModule.addDeserializer(ZonedDateTime.class, new DollyZonedDateTimeDeserializer());
        simpleModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ISO_DATE_TIME));
        simpleModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ISO_DATE));
        simpleModule.addSerializer(ZonedDateTime.class, new ZonedDateTimeSerializer(DateTimeFormatter.ISO_DATE_TIME));

        objectMapper.registerModule(simpleModule);
        return objectMapper;
    }

    private static class DollyLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
        @Override
        public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            if (isBlank(node.asText())) {
                return null;
            }

            String dateTime = node.asText().length() > TIME_STRING_LENGTH_TO_SECOND ? node.asText().substring(0, TIME_STRING_LENGTH_TO_SECOND) : node.asText();
            return dateTime.length() > TIME_STRING_LENGTH_TO_DATE ? LocalDateTime.parse(dateTime) : LocalDate.parse(dateTime).atStartOfDay();
        }
    }

    private static class DollyLocalDateDeserializer extends JsonDeserializer<LocalDate> {
        @Override
        public LocalDate deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            if (isBlank(node.asText())) {
                return null;
            }

            String dateTime = node.asText().length() > TIME_STRING_LENGTH_TO_DATE ? node.asText().substring(0, TIME_STRING_LENGTH_TO_DATE) : node.asText();
            return LocalDate.parse(dateTime);
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
}
