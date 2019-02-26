package no.nav.registre.syntrest.config;

import no.nav.registre.syntrest.controllers.domains.AaregController;
import no.nav.registre.syntrest.utils.Validation;
import org.springframework.context.annotation.*;
import org.springframework.web.client.RestTemplate;

@Configuration
@Import({ AaregController.class })//MeldekortController.class, EIAController.class, InntektController.class, EIAController.class, MedlController.class})
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