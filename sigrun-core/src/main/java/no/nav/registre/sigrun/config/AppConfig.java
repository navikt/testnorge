package no.nav.registre.sigrun.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

import no.nav.registre.sigrun.provider.rs.SyntetiseringController;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;

@Configuration
@Import({ SyntetiseringController.class })
public class AppConfig {

    @Value("${testnorge-hodejegeren.rest-api.url}")
    private String hodejegerenUrl;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    @DependsOn("restTemplate")
    public HodejegerenConsumer hodejegerenConsumer() {
        return new HodejegerenConsumer(hodejegerenUrl, restTemplate());
    }
}