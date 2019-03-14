package no.nav.registre.inst.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

import java.util.Random;

import no.nav.registre.inst.provider.rs.SyntetiseringController;

@Configuration
@Import({ SyntetiseringController.class })
public class AppConfig {

    @Bean
    public Random rand() {
        return new Random();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}