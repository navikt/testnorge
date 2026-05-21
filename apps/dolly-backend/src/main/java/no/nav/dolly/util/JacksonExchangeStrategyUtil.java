package no.nav.dolly.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.experimental.UtilityClass;
import org.springframework.http.codec.json.JacksonJsonDecoder;
import org.springframework.http.codec.json.JacksonJsonEncoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import static java.util.Objects.nonNull;

@UtilityClass
public final class JacksonExchangeStrategyUtil {

    public static ExchangeStrategies getJacksonStrategy() {
        return ExchangeStrategies.builder()
                .codecs(config -> config.defaultCodecs()
                        .maxInMemorySize(32 * 1024 * 1024))
                .build();
    }

    public static ExchangeStrategies getJacksonStrategy(JsonMapper jsonMapper) {

        var mapper = nonNull(jsonMapper) ? jsonMapper : createDefaultJsonMapper();
        return ExchangeStrategies.builder()
                .codecs(config -> {
                    config.defaultCodecs().maxInMemorySize(32 * 1024 * 1024);
                    config.defaultCodecs().jacksonJsonDecoder(new JacksonJsonDecoder(mapper));
                    config.defaultCodecs().jacksonJsonEncoder(new JacksonJsonEncoder(mapper));
                })
                .build();
    }

    public static ExchangeStrategies getJacksonStrategy(ObjectMapper objectMapper) {

        var jsonMapper = objectMapper instanceof JsonMapper jm ? jm : createDefaultJsonMapper();
        return ExchangeStrategies.builder()
                .codecs(config -> {
                    config.defaultCodecs().maxInMemorySize(32 * 1024 * 1024);
                    config.defaultCodecs().jacksonJsonDecoder(new JacksonJsonDecoder(jsonMapper));
                    config.defaultCodecs().jacksonJsonEncoder(new JacksonJsonEncoder(jsonMapper));
                })
                .build();
    }

    private static JsonMapper createDefaultJsonMapper() {
        return JsonMapper.builder()
                .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(JsonInclude.Include.NON_NULL))
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
                .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .build();
    }
}
