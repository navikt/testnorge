package no.nav.dolly.proxy.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
class FakedingsConfig {

    @Value("${app.fakedings.url}")
    private String url;

    @Bean
    FakedingsService fakedingsService(WebClient webClient) {
        return new FakedingsService(webClient, url);
    }

}
