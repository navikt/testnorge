package no.nav.dolly.libs.texas;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

@AutoConfiguration
@EnableConfigurationProperties(TexasConsumers.class)
@Slf4j
public class TexasAutoConfiguration {

    @Bean
    Texas texasService(
            WebClient webClient,
            @Value("${dolly.texas.url.token:}") String tokenUrl,
            @Value("${dolly.texas.url.exchange:}") String exchangeUrl,
            @Value("${dolly.texas.url.introspect:}") String introspectUrl,
            TexasConsumers texasConsumers
    ) {
        return new Texas(
                webClient,
                resolve(tokenUrl, "NAIS_TOKEN_ENDPOINT", "Neither dolly.texas.url.token nor NAIS_TOKEN_ENDPOINT is set"),
                resolve(exchangeUrl, "NAIS_TOKEN_EXCHANGE_ENDPOINT", "Neither dolly.texas.url.exchange nor NAIS_TOKEN_EXCHANGE_ENDPOINT is set"),
                resolve(introspectUrl, "NAIS_TOKEN_INTROSPECTION_ENDPOINT", "Neither dolly.texas.url.introspect nor NAIS_TOKEN_INTROSPECTION_ENDPOINT is set"),
                texasConsumers
        );
    }

    private static String resolve(String url, String fallback, String message)
            throws TexasException {
        return Optional
                .ofNullable(url)
                .or(() -> Optional.ofNullable(System.getProperty(fallback)))
                .orElseThrow(() -> new TexasException(message));
    }

}
