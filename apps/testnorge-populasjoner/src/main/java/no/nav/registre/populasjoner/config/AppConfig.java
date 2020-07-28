package no.nav.registre.populasjoner.config;

import static no.nav.common.utils.NaisUtils.getCredentials;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

import no.nav.common.utils.Credentials;

@Configuration
@Profile("!local")
public class AppConfig {

    @Bean
    public Credentials serviceUserCredentials() {
        return getCredentials("service_user");
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
