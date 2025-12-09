package no.nav.dolly.libs.test;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.function.client.WebClient;

@TestConfiguration
public class DollyTestSecurityConfiguration {

    @Bean
    @Primary
    public WebClient webClient() {
        return WebClient.builder().build();
    }
}

