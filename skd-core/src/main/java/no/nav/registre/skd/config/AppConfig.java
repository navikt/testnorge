package no.nav.registre.skd.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplateTpsf() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate;
    }
}
