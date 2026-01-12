package no.nav.dolly.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.json.JacksonJsonDecoder;
import org.springframework.http.codec.json.JacksonJsonEncoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@UtilityClass
public final class JacksonExchangeStrategyUtil {

    public static ExchangeStrategies getJacksonStrategy() {
        return ExchangeStrategies.builder()
                .codecs(config -> config.defaultCodecs()
                        .maxInMemorySize(32 * 1024 * 1024))
                .build();
    }

    public static ExchangeStrategies getJacksonStrategy(ObjectMapper objectMapper) {
        JsonMapper jsonMapper;
        if (objectMapper instanceof JsonMapper jm) {
            jsonMapper = jm;
            log.debug("JacksonExchangeStrategyUtil: Bruker eksisterende JsonMapper: {}", objectMapper.getClass().getName());
        } else {
            jsonMapper = JsonMapper.builder().build();
            log.warn("JacksonExchangeStrategyUtil: ObjectMapper er ikke JsonMapper ({}), oppretter ny JsonMapper uten konfigurasjon", 
                    objectMapper != null ? objectMapper.getClass().getName() : "null");
        }
        return ExchangeStrategies.builder()
                .codecs(config -> {
                    config.defaultCodecs().maxInMemorySize(32 * 1024 * 1024);
                    config.defaultCodecs().jacksonJsonDecoder(new JacksonJsonDecoder(jsonMapper));
                    config.defaultCodecs().jacksonJsonEncoder(new JacksonJsonEncoder(jsonMapper));
                })
                .build();
    }
}
