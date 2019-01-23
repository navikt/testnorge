package no.nav.registre.sigrun.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

import no.nav.registre.sigrun.provider.rs.SyntetiseringController;

@Configuration
@Import({ SyntetiseringController.class })
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}