package no.nav.registre.testnorge.arena.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

import java.util.Random;

import no.nav.registre.testnorge.libs.core.config.ApplicationCoreConfig;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;

import no.nav.registre.testnorge.libs.service.StsOidcTokenService;

@Configuration
@Import(ApplicationCoreConfig.class)
public class AppConfig {

    @Value("${testnorge-hodejegeren.rest-api.url}")
    private String hodejegerenUrl;

    @Bean
    public StsOidcTokenService stsOidcTokenService(
            @Value("${sts.token.provider.url}") String url,
            @Value("${sts.token.provider.username}") String username,
            @Value("${sts.token.provider.password}") String password
    ) {
        return new StsOidcTokenService(url, username, password);
    }

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
        return new HodejegerenConsumer(hodejegerenUrl, restTemplate());
    }
}
