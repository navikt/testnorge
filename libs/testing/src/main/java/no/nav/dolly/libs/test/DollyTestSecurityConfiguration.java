package no.nav.dolly.libs.test;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@TestConfiguration
public class DollyTestSecurityConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public WebClient testWebClient() {
        return WebClient.builder().build();
    }
}
