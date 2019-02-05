package no.nav.registre.syntrest.config;

import no.nav.registre.syntrest.controllers.*;
import no.nav.registre.syntrest.globals.QueueHandler;
import no.nav.registre.syntrest.utils.Validation;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.*;
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

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public QueueHandler queueHandler(){
        return new QueueHandler();
    }
}