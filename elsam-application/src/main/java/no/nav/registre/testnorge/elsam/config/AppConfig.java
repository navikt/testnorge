package no.nav.registre.testnorge.elsam.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.client.RestTemplate;

import java.util.Random;

import no.nav.registre.testnorge.consumers.ConsumerFactory;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public Random random() {
        return new Random();
    }

    @Bean
    @DependsOn("restTemplate")
    public HodejegerenConsumer hodejegerenConsumer() {
        return (HodejegerenConsumer) ConsumerFactory.create(HodejegerenConsumer.class, restTemplate());
    }
}