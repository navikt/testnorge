package no.nav.dolly.config;

import static org.mockito.Mockito.mock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

@Configuration
public class TestConfig {

    @Bean
    @Primary
    public RestTemplate mockRestTemplate() {
        return mock(RestTemplate.class);
    }

}
