package no.nav.registre.populasjoner.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

import no.nav.common.utils.Credentials;

@Configuration
@Profile("test")
public class TestConfig {

    @Bean
    public Credentials serviceUserCredentials() {
        return new Credentials("username", "password");
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
