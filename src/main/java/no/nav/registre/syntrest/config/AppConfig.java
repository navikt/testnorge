package no.nav.registre.syntrest.config;

import no.nav.registre.syntrest.providers.ArenaInntektController;
import no.nav.registre.syntrest.providers.InternalController;
import no.nav.registre.syntrest.providers.MedlController;
import no.nav.registre.syntrest.providers.MeldekortController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

@Configuration
@Import({ MeldekortController.class, ArenaInntektController.class, InternalController.class, MedlController.class})
public class AppConfig {

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

}