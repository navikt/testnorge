package no.nav.dolly.budpro.texas;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

@Configuration
@Slf4j
public class TexasAutoConfiguration {

    @Bean
    TexasService texasService(
            WebClient webClient,
            @Value("${dolly.texas.url.token:}") String url
    ) {
        var texasUrl = Optional
                .ofNullable(url)
                .orElseGet(() -> Optional
                        .ofNullable(System.getProperty("NAIS_TOKEN_ENDPOINT"))
                        .orElseThrow(() -> new IllegalStateException("Neither dolly.texas.url.token or NAIS_TOKEN_ENDPOINT is set")));
        var texasWebClient = webClient
                .mutate()
                .baseUrl(texasUrl)
                .build();
        return new TexasService(texasWebClient);
    }

}
