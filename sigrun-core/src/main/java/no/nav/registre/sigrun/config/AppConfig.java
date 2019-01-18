package no.nav.registre.sigrun.config;

import no.nav.registre.sigrun.globals.RestConnections;
import no.nav.registre.sigrun.provider.rs.SyntetiseringController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;


@Configuration
@Import({SyntetiseringController.class})
public class AppConfig {

    @Bean
    public RestTemplate restTemplateTpsf() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate;
    }

    @Bean
    public RestConnections restConnections(){
        RestConnections restConnections = new RestConnections();
        return restConnections;
    }
}