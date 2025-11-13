package no.nav.dolly.proxy.auth;

import no.nav.testnav.libs.reactivesecurity.exchange.tokenx.TokenXService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
class FakedingsConfig {

    @Value("${app.fakedings.url}")
    private String url;

    @Bean
    FakedingsService fakedingsService(TokenXService tokenXService, WebClient webClient) {
        return new FakedingsService(tokenXService, webClient, url);
    }

}
