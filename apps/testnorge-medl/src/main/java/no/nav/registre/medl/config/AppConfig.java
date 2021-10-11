package no.nav.registre.medl.config;

import no.nav.testnav.libs.servletsecurity.config.SecureOAuth2ServerToServerConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

import no.nav.testnav.libs.servletcore.config.ApplicationCoreConfig;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;

@Configuration
@Import(value = {
        ApplicationCoreConfig.class,
        SecureOAuth2ServerToServerConfiguration.class
})
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