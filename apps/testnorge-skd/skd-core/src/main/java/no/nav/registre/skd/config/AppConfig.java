package no.nav.registre.skd.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

import java.util.Random;

import no.nav.registre.testnorge.libs.core.config.ApplicationCoreConfig;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;
import no.nav.registre.testnorge.libs.oauth2.config.InsecureOAuth2ServerToServerConfiguration;

@Configuration
@Import(value = {ApplicationCoreConfig.class, InsecureOAuth2ServerToServerConfiguration.class})
public class AppConfig {

    @Value("${testnorge-hodejegeren.rest-api.url}")
    private String hodejegerenUrl;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public Random rand() {
        return new Random();
    }

    @Bean
    @DependsOn("restTemplate")
    public HodejegerenConsumer hodejegerenConsumer() {
        return new HodejegerenConsumer(hodejegerenUrl, restTemplate());
    }
}
