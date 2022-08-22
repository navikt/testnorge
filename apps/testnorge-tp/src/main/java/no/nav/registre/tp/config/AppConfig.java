package no.nav.registre.tp.config;

import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;
import no.nav.testnav.libs.servletcore.config.ApplicationCoreConfig;
import no.nav.testnav.libs.standalone.servletsecurity.config.InsecureJwtServerToServerConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

@Configuration
@Import(value = {
        ApplicationCoreConfig.class,
        InsecureJwtServerToServerConfiguration.class
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
