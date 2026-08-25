package no.nav.dolly.util;

import lombok.experimental.UtilityClass;
import org.springframework.http.codec.json.JacksonJsonDecoder;
import org.springframework.http.codec.json.JacksonJsonEncoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import tools.jackson.databind.json.JsonMapper;

@UtilityClass
public final class JacksonExchangeStrategyUtil {

    public static ExchangeStrategies getJacksonStrategy() {
        return ExchangeStrategies.builder()
                .codecs(config -> config.defaultCodecs()
                        .maxInMemorySize(32 * 1024 * 1024))
                .build();
    }

    public static ExchangeStrategies getJacksonStrategy(JsonMapper jsonMapper) {

        return ExchangeStrategies.builder()
                .codecs(config -> {
                    config.defaultCodecs().maxInMemorySize(32 * 1024 * 1024);
                    config.defaultCodecs().jacksonJsonDecoder(new JacksonJsonDecoder(jsonMapper));
                    config.defaultCodecs().jacksonJsonEncoder(new JacksonJsonEncoder(jsonMapper));
                })
                .build();
    }
}
