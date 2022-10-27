package no.nav.registre.testnorge.helsepersonellservice.util;

import lombok.experimental.UtilityClass;
import org.springframework.web.reactive.function.client.ExchangeStrategies;

@UtilityClass
public final class ExhangeStrategyUtil {

    public static ExchangeStrategies biggerMemorySizeExchangeStrategy() {
        return ExchangeStrategies.builder()
                .codecs(config -> config.defaultCodecs()
                        .maxInMemorySize(32 * 1024 * 1024)).build();
    }

}