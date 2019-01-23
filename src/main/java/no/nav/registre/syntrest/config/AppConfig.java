package no.nav.registre.syntrest.config;

import no.nav.registre.syntrest.controllers.*;
import no.nav.registre.syntrest.globals.QueueHandler;
import no.nav.registre.syntrest.utils.Validation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

@Configuration
@Import({ MeldekortController.class, EIAController.class, ArenaInntektController.class, EIAController.class, MedlController.class})
public class AppConfig {

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    Validation validation() {
        return new Validation();
    }
}