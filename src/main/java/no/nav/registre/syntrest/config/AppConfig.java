package no.nav.registre.syntrest.config;

import no.nav.registre.syntrest.controllers.*;
import no.nav.registre.syntrest.services.EIAService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

@Configuration
@Import({ MeldekortController.class})
public class AppConfig {

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

}