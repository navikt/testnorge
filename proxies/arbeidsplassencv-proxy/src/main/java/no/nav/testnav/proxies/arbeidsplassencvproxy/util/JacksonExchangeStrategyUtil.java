package no.nav.testnav.proxies.arbeidsplassencvproxy.util;

import lombok.experimental.UtilityClass;
import org.springframework.web.reactive.function.client.ExchangeStrategies;

@UtilityClass
public final class JacksonExchangeStrategyUtil {

    public static ExchangeStrategies getJacksonStrategy() {
        return ExchangeStrategies.builder()
                .codecs(config -> config.defaultCodecs()
                        .maxInMemorySize(32 * 1024 * 1024))
                .build();
    }
}
