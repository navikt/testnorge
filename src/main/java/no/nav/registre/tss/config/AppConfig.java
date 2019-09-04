package no.nav.registre.tss.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import no.nav.registre.testnorge.consumers.ConsumerFactory;
import no.nav.registre.testnorge.consumers.HodejegerenConsumer;

@Configuration
@Profile("local")
public class AppConfig {

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
    }

    @Bean HodejegerenConsumer hodejegerenConsumer(){
        return (HodejegerenConsumer) ConsumerFactory.create(HodejegerenConsumer.class, restTemplate());
    }
}