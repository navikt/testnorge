package no.nav.registre.skd.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

import java.util.Random;

import no.nav.registre.skd.consumer.TpsSyntetisererenConsumer;

@Configuration
@Import(TpsSyntetisererenConsumer.class)
public class AppConfig {

    @Bean
    public RestTemplate restTemplateTpsf() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate;
    }

    @Bean
    public Random rand() {
        return new Random();
    }
}
